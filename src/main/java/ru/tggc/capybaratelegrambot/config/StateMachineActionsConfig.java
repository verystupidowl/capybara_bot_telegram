package ru.tggc.capybaratelegrambot.config;

import com.pengrad.telegrambot.TelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.tggc.capybaratelegrambot.domain.dto.fight.BossFightState;
import ru.tggc.capybaratelegrambot.domain.sm.BossFightEvents;
import ru.tggc.capybaratelegrambot.domain.sm.BossFightStates;
import ru.tggc.capybaratelegrambot.service.BossFightMessageSender;
import ru.tggc.capybaratelegrambot.service.BossFightService;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class StateMachineActionsConfig {
    private final BossFightMessageSender bossFightMessageSender;

    @Setter(onMethod_ = {@Autowired, @Lazy})
    private BossFightService bossFightService;

    public Action<BossFightStates, BossFightEvents> sendMessagesAction() {
        return context -> {
            var vars = context.getExtendedState();

            BossFightState fight = vars.get("fight", BossFightState.class);
            Long chatId = vars.get("chatId", Long.class);
            Integer messageId = vars.get("messageId", Integer.class);
            TelegramBot bot = vars.get("bot", TelegramBot.class);
            CompletableFuture.runAsync(() -> {
                bossFightMessageSender.sendMessages(chatId, messageId, fight, bot);

                CompletableFuture<Void> responseFuture = vars.get("responseFuture", CompletableFuture.class);
                if (responseFuture != null) responseFuture.complete(null);
            });
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

    public Action<BossFightStates, BossFightEvents> bossTurnAction() {
        return context -> {
            BossFightState fight = context.getExtendedState().get("fight", BossFightState.class);
            log.info("doing boss actions {}", fight);
            bossFightService.doBossAction(fight);
            context.getStateMachine().sendEvent(Mono.just(MessageBuilder.withPayload(BossFightEvents.BOSS_DONE).build())).subscribe();
        };
    }

    public Action<BossFightStates, BossFightEvents> finishBattleAction() {
        return context -> {
            var vars = context.getExtendedState();

            BossFightState fight = vars.get("fight", BossFightState.class);
            long chatId = vars.get("chatId", Long.class);
            TelegramBot bot = vars.get("bot", TelegramBot.class);
            Integer messageId = vars.get("messageId", Integer.class);

            boolean bossDead = fight.getBossState().getBossHp() <= 0;
            CompletableFuture.runAsync(() -> {
                bossFightService.finishFight(chatId, context.getStateMachine());
                bossFightMessageSender.sendFinishMessage(bossDead, chatId, bot, messageId);
                CompletableFuture<Void> responseFuture = vars.get("responseFuture", CompletableFuture.class);
                if (responseFuture != null) responseFuture.complete(null);
            });
        };
    }
}
