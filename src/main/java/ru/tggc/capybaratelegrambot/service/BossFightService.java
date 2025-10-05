package ru.tggc.capybaratelegrambot.service;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageCaption;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.dto.UserDto;
import ru.tggc.capybaratelegrambot.domain.dto.fight.BossFightState;
import ru.tggc.capybaratelegrambot.domain.dto.fight.enums.BossAction;
import ru.tggc.capybaratelegrambot.domain.dto.fight.enums.BossType;
import ru.tggc.capybaratelegrambot.domain.dto.fight.enums.PlayerActionType;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.Fight;
import ru.tggc.capybaratelegrambot.domain.response.Response;
import ru.tggc.capybaratelegrambot.domain.sm.BossFightEvents;
import ru.tggc.capybaratelegrambot.domain.sm.BossFightStates;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.provider.BossFightProvider;
import ru.tggc.capybaratelegrambot.utils.RandomUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static ru.tggc.capybaratelegrambot.utils.Utils.throwIf;

@Service
@Slf4j
@RequiredArgsConstructor
public class BossFightService {
    private final BossFightProvider provider;
    private final CapybaraService capybaraService;
    private final InlineKeyboardCreator inlineKeyboardCreator;
    private final UserRateLimiterService userRateLimiterService;
    private final TimedActionService timedActionService;
    private final StateMachineFactory<BossFightStates, BossFightEvents> stateMachineFactory;


    public static final List<String> ATTACK_TEXTS = List.of(
            "⚔️ %s прыгнул на босса и вцепился зубами! Урон: %d",
            "💥 %s с размаху ударил хвостом по боссу! Урон: %d",
            "🔥 %s атакует со всей силы! Урон: %d"
    );
    public static final List<String> DEFEND_TEXTS = List.of(
            "🛡️ %s встал в оборону и приготовился к удару",
            "🌊 %s прячется за камышами и снижает входящий урон",
            "🪵 %s нашёл бревно и использует его как щит"
    );
    public static final List<String> HEAL_TEXTS = List.of(
            "🌿 %s жует свежую травку и восстанавливает %d HP",
            "💧 %s сделал глоток прохладной воды и восстановил %d HP",
            "✨ %s вдохнул силы природы и восстановил %d HP"
    );

    public String joinFight(CapybaraContext ctx, String username) {
        Capybara capybara = capybaraService.getFightCapybara(ctx.chatId(), ctx.userId());
        throwIf(!capybara.getFight().getFightAction().canPerform(), () -> new CapybaraException("u will can join only in " + timedActionService.getStatus(capybara.getFight().getFightAction())));
        return provider.joinFight(ctx.chatId(), ctx.userId(), username);
    }

    public void leaveFight(Long chatId, Long userId) {
        provider.leaveFight(chatId, userId);
    }

    public String startFight(Long chatId) {
        StateMachine<BossFightStates, BossFightEvents> sm = stateMachineFactory.getStateMachine(chatId.toString());
        Optional<StateMachine<BossFightStates, BossFightEvents>> optional = provider.getFight(chatId);
        throwIf(optional.isPresent(), () -> new CapybaraException("Файт уже идет"));
        BossFightState fight = new BossFightState();
        BossType bossType = RandomUtils.geetRandomBoss();
        BossFightState.BossState bossState = BossFightState.BossState.builder()
                .bossType(bossType)
                .bossHp(bossType.getHp())
                .build();
        fight.setBossState(bossState);
        fight.setTurn(1);
        fight.setPlayers(new HashMap<>());
        fight.setActionLogs(new ArrayList<>());

        Set<UserDto> users = provider.popPreparedUsers(chatId);
        users.forEach(user -> {
            Capybara fightCapybara = capybaraService.getFightCapybara(chatId, user.userId());
            BossFightState.PlayerStats playerStates = createPlayerStates(fightCapybara);
            BossFightState.PlayerState ps = BossFightState.PlayerState.builder()
                    .userId(user.userId())
                    .username(user.username())
                    .playerStats(playerStates)
                    .alive(true)
                    .stunned(false)
                    .lastAction(null)
                    .boss(bossState)
                    .build();
            fight.getPlayers().put(user.userId(), ps);

            sm.startReactively()
                    .then(Mono.defer(() -> {
                        sm.getExtendedState().getVariables().put("fight", fight);
                        sm.getExtendedState().getVariables().put("chatId", chatId);
                        return Mono.empty();
                    }))
                    .subscribe();
        });

        provider.startFight(chatId, sm);
        return "Бой начинается!⚔️\nBoss: " + bossType.getName() + " hp: " + bossType.getHp();
    }


    public Response registerAction(CallbackQuery query, UserDto userDto, PlayerActionType action) {
        long chatId = query.maybeInaccessibleMessage().chat().id();

        return provider.getFight(chatId)
                .map(sm -> {
                    BossFightState fight = sm.getExtendedState().get("fight", BossFightState.class);
                    long playerId = userDto.userId();
                    BossFightState.PlayerState ps = fight.getPlayers().get(playerId);
                    Integer messageId = query.maybeInaccessibleMessage().messageId();
                    sm.getExtendedState().getVariables().put("messageId", messageId);

                    if (ps == null || !ps.isAlive()) {
                        return Response.of(new SendMessage(chatId, "Твоя капибара без сознания или не участвует!"));
                    }

                    ps.setLastAction(action);
                    log.info("Игрок {} выбрал {}", ps.getUsername(), action);

                    boolean allChosen = fight.getPlayers().values().stream()
                            .filter(BossFightState.PlayerState::isAlive)
                            .filter(playerState -> !playerState.isStunned())
                            .allMatch(p -> p.getLastAction() != null);

                    if (allChosen) {
                        return Response.of(new AnswerCallbackQuery(query.id()))
                                .andThen(bot -> {
                                    fight.getPlayers().values()
                                            .forEach(p -> userRateLimiterService.lock(p.getUserId()));
                                    CompletableFuture<Void> future = new CompletableFuture<>();

                                    sm.getExtendedState().getVariables().put("responseFuture", future);
                                    sm.getExtendedState().getVariables().put("bot", bot);
                                    sendEvents(sm, fight);

                                    return future;
                                })
                                .andThen(bot -> {
                                    fight.getPlayers().values()
                                            .forEach(p -> userRateLimiterService.unlock(p.getUserId()));
                                    return new CompletableFuture<>();
                                });
                    }
                    String text = ((Message) query.maybeInaccessibleMessage())
                            .caption() + "\n==========================\n⌛ Ждём действий от всех игроков";
                    EditMessageCaption message = new EditMessageCaption(chatId, messageId)
                            .caption(text)
                            .replyMarkup(inlineKeyboardCreator.fightKeyboard());
                    return Response.of(message);
                }).orElseGet(() -> Response.of(new SendMessage(chatId, "⚠️ Бой не найден."))
                        .andThen(Response.of(new DeleteMessage(chatId, query.maybeInaccessibleMessage().messageId()))));
    }

    private void sendEvents(StateMachine<BossFightStates, BossFightEvents> sm, BossFightState fight) {
        sm.sendEvent(Mono.just(MessageBuilder.withPayload(BossFightEvents.PLAYERS_CHOSE).build()))
                .thenMany(sm.sendEvent(Mono.just(MessageBuilder.withPayload(BossFightEvents.BOSS_DONE).build())))
                .thenMany(sm.sendEvent(Mono.just(MessageBuilder.withPayload(BossFightEvents.PLAYERS_DONE).build())))
                .thenMany(Flux.defer(() -> {
                    boolean allPlayersDead = fight.getPlayers().values().stream()
                            .noneMatch(BossFightState.PlayerState::isAlive);
                    boolean bossDead = fight.getBossState().getBossHp() <= 0;

                    if (allPlayersDead || bossDead) {
                        return sm.sendEvent(Mono.just(MessageBuilder.withPayload(BossFightEvents.BATTLE_FINISHED).build()));
                    } else {
                        return sm.sendEvent(Mono.just(MessageBuilder.withPayload(BossFightEvents.TURN_FINISHED).build()));
                    }
                })).subscribe();
    }


    public void doBossAction(BossFightState fight) {
        BossAction bossAction = RandomUtils.getRandomBossAction(fight.getBossState().getBossType());
        List<BossFightState.PlayerState> alivePlayers = fight.getPlayers().values().stream()
                .filter(BossFightState.PlayerState::isAlive)
                .toList();

        if (alivePlayers.isEmpty()) return;

        fight.getActionLogs().add("🐊 Ход босса:\n" +
                bossAction.apply(fight, alivePlayers) +
                checkPs(alivePlayers));
    }

    public void doPlayerAction(BossFightState fight) {
        StringBuilder response = new StringBuilder();
        Collection<BossFightState.PlayerState> players = fight.getPlayers().values();

        for (BossFightState.PlayerState ps : players) {
            if (!ps.isAlive() || ps.isStunned()) {
                if (ps.isStunned()) {
                    response.append("😵 ").append(ps.getUsername())
                            .append(" оглушён и пропускает ход!\n");
                    ps.endTurn();
                }
                continue;
            }

            response.append(ps.getLastAction().apply(fight, ps));
            ps.endTurn();
        }
        response.append("==========================\n").append(getPlayersHp(players, fight.getBossState()));
        fight.getActionLogs().add(response.toString());
    }

    private String getPlayersHp(Collection<BossFightState.PlayerState> players, BossFightState.BossState boss) {
        return players.stream().map(ps -> {
            StringBuilder sb = new StringBuilder();
            sb.append("HP❤️").append(ps.getUsername()).append(": ");
            String hp = ps.getPlayerStats().getHp() >= 0 ? String.valueOf(ps.getPlayerStats().getHp()) : "0☠";
            sb.append(hp);

            return sb.toString();
        }).collect(Collectors.joining("\n")) + "\n" + "HP❤️ босса: " + boss.getBossHp();
    }

    private static String checkPs(List<BossFightState.PlayerState> alivePlayers) {
        StringBuilder sb = new StringBuilder();
        for (BossFightState.PlayerState ps : alivePlayers) {
            if (ps.getPlayerStats().getHp() <= 0) {
                ps.setAlive(false);
                sb.append(ps.getUsername()).append(" Твоя капибара упала без сознания!");
            }
        }
        return sb.toString();
    }

    public String getUsers(CapybaraContext ctx) {
        Set<UserDto> users = provider.getPreparedUsers(ctx.chatId());
        return "Ты уверен? Ваша команда - " + users.size() + " капибар:\n" + users.stream()
                .map(UserDto::username)
                .collect(Collectors.joining("\n"));
    }

    private BossFightState.PlayerStats createPlayerStates(Capybara fightCapybara) {
        Integer level = fightCapybara.getLevel().getValue();
        BossFightState.PlayerStats stats = BossFightState.PlayerStats.builder()
                .hp(100 + level / 10)
                .baseDamage(100 + ((double) level / 5))
                .baseDefend(0.5)
                .baseHeal(20)
                .critChance(0.15)
                .vampirism(0)
                .effects(new HashSet<>())
                .build();
        Fight fight = fightCapybara.getFight();
        fight.getWeapon().apply(stats);
        fight.getShield().apply(stats);
        fight.getHeal().apply(stats);
        fight.getSpecial().apply(stats);
        return stats;
    }

    public void finishFight(long chatId, StateMachine<BossFightStates, BossFightEvents> sm) {
        sm.getExtendedState().getVariables().clear();
        sm.stopReactively().block();
        provider.endFight(chatId);
    }
}
