package ru.tggc.capybaratelegrambot.config.sm;

import com.pengrad.telegrambot.TelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.tggc.capybaratelegrambot.domain.dto.fight.BossFightState;
import ru.tggc.capybaratelegrambot.domain.sm.event.BossFightEvents;
import ru.tggc.capybaratelegrambot.domain.sm.state.BossFightStates;
import ru.tggc.capybaratelegrambot.service.BossFightMessageSender;
import ru.tggc.capybaratelegrambot.service.BossFightService;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class FightStateMachineActionsConfig {
    private final BossFightMessageSender bossFightMessageSender;

    @Setter(onMethod_ = {@Autowired, @Lazy})
    private BossFightService bossFightService;

    public Action<BossFightStates, BossFightEvents> bossTurnAction() {
        return context -> {
            BossFightState fight = context.getExtendedState().get("fight", BossFightState.class);
            log.info("doing boss actions {}", fight);
            bossFightService.doBossAction(fight);
            context.getStateMachine().sendEvent(Mono.just(MessageBuilder.withPayload(BossFightEvents.BOSS_DONE).build())).subscribe();
        };
    }

    public Action<BossFightStates, BossFightEvents> playerTurnAction() {
        return context -> {
            BossFightState fight = context.getExtendedState().get("fight", BossFightState.class);
            log.info("doing players actions {}", fight);
            bossFightService.doPlayerAction(fight);
            context.getStateMachine().sendEvent(Mono.just(MessageBuilder.withPayload(BossFightEvents.PLAYERS_DONE).build())).subscribe();
        };
    }

    public Action<BossFightStates, BossFightEvents> sendMessagesAction() {
        return context -> {
            var vars = context.getExtendedState();

            BossFightState fight = vars.get("fight", BossFightState.class);
            Long chatId = vars.get("chatId", Long.class);
            Integer messageId = vars.get("messageId", Integer.class);
            TelegramBot bot = vars.get("bot", TelegramBot.class);

            log.info("sending messages: [{}]", fight.getActionLogs());

            CompletableFuture.runAsync(() -> {
                int newMessageId = bossFightMessageSender.sendMessages(chatId, messageId, fight, bot);
                vars.getVariables().put("messageId", newMessageId);
                CompletableFuture<Void> responseFuture = vars.get("responseFuture", CompletableFuture.class);
                if (responseFuture != null) responseFuture.complete(null);
            });
        };
    }

    public Action<BossFightStates, BossFightEvents> finishBattleAction() {
        return context -> {
            var vars = context.getExtendedState();
            log.info("sending finish message");

            BossFightState fight = vars.get("fight", BossFightState.class);
            long chatId = vars.get("chatId", Long.class);
            TelegramBot bot = vars.get("bot", TelegramBot.class);
            Integer messageId = vars.get("messageId", Integer.class);

            boolean bossDead = fight.getBossState().getBossHp() <= 0;
            try {
                int cost = bossFightService.finishFight(chatId, fight, bossDead);
                bossFightMessageSender.sendFinishMessage(fight.getActionLogs(), bossDead, chatId, bot, messageId, cost);
            } catch (Exception e) {
                log.error("error", e);
            }

            CompletableFuture<Void> responseFuture = vars.get("responseFuture", CompletableFuture.class);
            if (responseFuture != null) responseFuture.complete(null);
            StateMachine<BossFightStates, BossFightEvents> sm = context.getStateMachine();
            sm.getExtendedState().getVariables().clear();
            sm.stopReactively().block();
        };
    }
}
