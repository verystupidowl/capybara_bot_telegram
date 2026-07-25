package ru.tggc.botapp.service.bossfight;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageCaption;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.Fight;
import ru.tggc.botapp.exceptions.CapybaraException;
import ru.tggc.botapp.fight.BossFightState;
import ru.tggc.botapp.fight.enums.BossAction;
import ru.tggc.botapp.fight.enums.BossType;
import ru.tggc.botapp.fight.enums.PlayerActionType;
import ru.tggc.botapp.fight.event.boss.BossActionResult;
import ru.tggc.botapp.formatter.msgkey.FightEventMsgKey;
import ru.tggc.botapp.formatter.msgkey.FightMsgKey;
import ru.tggc.botapp.keyboard.KeyboardType;
import ru.tggc.botapp.provider.BossFightProvider;
import ru.tggc.botapp.service.CapybaraService;
import ru.tggc.botapp.service.TimedActionService;
import ru.tggc.botapp.util.RandomUtils;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.dto.UserDto;
import ru.tggc.telegrambotcore.formatter.FormatService;
import ru.tggc.telegrambotcore.formatter.MsgKey;
import ru.tggc.telegrambotcore.keyboard.KeyboardFactory;
import ru.tggc.telegrambotcore.service.UserRateLimiterService;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static ru.tggc.telegrambotcore.util.Utils.throwIf;

@Service
@Slf4j
@RequiredArgsConstructor
public class BossFightService {
    private final BossFightProvider provider;
    private final CapybaraService capybaraService;
    private final KeyboardFactory keyboardFactory;
    private final UserRateLimiterService userRateLimiterService;
    private final TimedActionService timedActionService;
    private final BossFightMessageSender messageSender;
    private final FormatService formatService;

    @Setter(onMethod = @__({@Lazy, @Autowired}))
    private BossFightService self;

    public String joinFight(UpdateContext ctx, String username) {
        Capybara capybara = capybaraService.getFightCapybara(ctx.chatId(), ctx.userId());
        throwIf(!capybara.getFight().getFightAction().canPerform(), () -> {
            String message = "u will can join only in " + timedActionService.getStatus(capybara.getFight().getFightAction());
            return new CapybaraException(message);
        });
        return provider.joinFight(ctx.chatId(), ctx.userId(), username);
    }

    public void leaveFight(Long chatId, Long userId) {
        provider.leaveFight(chatId, userId);
    }

    public String startFight(Long chatId) {
        Optional<BossFightState> optional = provider.getFight(chatId);
        throwIf(optional.isPresent(), () -> new CapybaraException("Fight already in progress"));

        BossType bossType = RandomUtils.getRandomBoss();
        BossFightState.BossState bossState = BossFightState.BossState.builder()
                .bossType(bossType)
                .bossHp(bossType.getHp())
                .build();

        BossFightState fight = new BossFightState(bossState);

        Set<UserDto> users = provider.popPreparedUsers(chatId);
        users.forEach(user -> {
            Capybara fightCapybara = capybaraService.getFightCapybara(chatId, user.userId());
            BossFightState.PlayerStats playerStates = createPlayerStates(fightCapybara);
            BossFightState.PlayerState ps = BossFightState.PlayerState.builder()
                    .userId(user.userId())
                    .username(user.username())
                    .playerStats(playerStates)
                    .alive(true)
                    .canAct(true)
                    .lastAction(null)
                    .boss(bossState)
                    .specials(1)
                    .build();
            fight.getPlayers().put(user.userId(), ps);
        });

        provider.startFight(chatId, fight);
        return formatService.get(FightMsgKey.START_MESSAGE, bossType.getName(), bossType.getHp());
    }

    public String getUsers(UpdateContext ctx) {
        Set<UserDto> users = provider.getPreparedUsers(ctx.chatId());
        String usernames = users.stream()
                .map(UserDto::username)
                .collect(Collectors.joining("\n"));
        return formatService.get(FightMsgKey.PREPARING_USERS, users.size(), usernames);
    }

    public Response registerAction(CallbackQuery query, UserDto userDto, PlayerActionType action) {
        long chatId = query.maybeInaccessibleMessage().chat().id();

        return provider.getFight(chatId)
                .map(fight -> {
                    long playerId = userDto.userId();
                    BossFightState.PlayerState ps = fight.getPlayers().get(playerId);
                    Integer messageId = query.maybeInaccessibleMessage().messageId();

                    if (ps == null || !ps.isAlive()) {
                        return Response.of(new SendMessage(chatId, formatService.get(FightMsgKey.CANT_ACT)).parseMode(ParseMode.HTML));
                    }

                    if (ps.getLastAction() != null) {
                        return Response.empty();
                    }
                    ps.setLastAction(action);
                    log.info("Игрок {} выбрал {}", ps.getUsername(), action);

                    boolean allChosen = fight.getPlayers().values().stream()
                            .filter(BossFightState.PlayerState::isAlive)
                            .filter(BossFightState.PlayerState::isCanAct)
                            .allMatch(p -> p.getLastAction() != null);

                    if (allChosen) {
                        if (!userRateLimiterService.tryLock(chatId)) {
                            return Response.of(new AnswerCallbackQuery(query.id()));
                        }

                        return Response.of(new AnswerCallbackQuery(query.id()))
                                .andThen(bot -> {
                                    try {
                                        self.processTurnLogic(chatId, messageId, fight, bot);
                                    } finally {
                                        userRateLimiterService.unlock(chatId);
                                    }
                                    return CompletableFuture.completedFuture(null);
                                });
                    }
                    String caption = ((Message) query.maybeInaccessibleMessage()).caption();
                    String text = formatService.get(FightMsgKey.PLAYER_CHOSE, caption, ps.getUsername(), action.getLabel());
                    EditMessageCaption message = new EditMessageCaption(chatId, messageId)
                            .caption(text)
                            .replyMarkup(keyboardFactory.getKeyboardInline(KeyboardType.FIGHT));
                    return Response.of(message);
                }).orElseGet(() -> Response.of(new SendMessage(chatId, "⚠️ Бой не найден.").parseMode(ParseMode.HTML))
                        .andThen(Response.of(new DeleteMessage(chatId, query.maybeInaccessibleMessage().messageId()))));
    }

    @Transactional
    public void processTurnLogic(long chatId, Integer messageId, BossFightState fight, TelegramBot bot) {
        doPlayerAction(fight);

        boolean bossDead = fight.getBossState().getBossHp() <= 0;
        if (bossDead) {
            int reward = finishFight(chatId, fight, true);
            messageSender.sendFinishMessage(fight.getActionLogs(), true, chatId, bot, messageId, reward);
            return;
        }

        doBossAction(fight);

        boolean allDead = fight.getPlayers().values().stream().noneMatch(BossFightState.PlayerState::isAlive);
        if (allDead) {
            finishFight(chatId, fight, false);
            messageSender.sendFinishMessage(fight.getActionLogs(), false, chatId, bot, messageId, 0);
            return;
        }

        messageSender.sendMessages(chatId, messageId, fight, bot);
    }

    public void doBossAction(BossFightState fight) {
        BossAction bossAction = RandomUtils.getRandomBossAction(fight.getBossState().getBossType());
        List<BossFightState.PlayerState> alivePlayers = fight.getPlayers().values().stream()
                .filter(BossFightState.PlayerState::isAlive)
                .toList();

        if (alivePlayers.isEmpty()) {
            return;
        }

        BossActionResult actionResult = bossAction.apply(alivePlayers);
        StringBuilder response = new StringBuilder();
        actionResult.events().forEach(event -> {
            MsgKey msgKey = FightEventMsgKey.getMsgKeyByEvent(event.getClass());
            String format = formatService.get(msgKey, event.getArgs());
            response.append(format).append("\n");
        });
        String checkedPlayers = checkPs(alivePlayers);
        fight.getActionLogs().add(formatService.get(
                FightMsgKey.BOSS_ACTION_TEMPLATE,
                response.toString(),
                checkedPlayers,
                getPlayersHp(fight.getPlayers().values(), fight.getBossState())
        ));
        if (fight.getPlayers().values().stream().noneMatch(BossFightState.PlayerState::isAlive)) {
            fight.getActionLogs().add("Boss won!");
        }
    }

    public void doPlayerAction(BossFightState fight) {
        StringBuilder response = new StringBuilder("Ход игроков:\n");
        Collection<BossFightState.PlayerState> players = fight.getPlayers().values();

        for (BossFightState.PlayerState ps : players) {
            ps.getPlayerStats().getEffects().forEach(e -> e.onTurnBegin(ps));
            if (!ps.isAlive() || !ps.isCanAct()) {
                if (ps.isAlive()) {
                    response.append("😵 ").append(ps.getUsername())
                            .append(" оглушён и пропускает ход!\n");
                    ps.endTurn();
                }
                continue;
            }

            ps.getLastAction().apply(fight, ps).events().forEach(event -> {
                MsgKey msgKey = FightEventMsgKey.getMsgKeyByEvent(event.getClass());
                String action = formatService.get(msgKey, event.getArgs());
                response.append(action).append("\n");
            });

            ps.endTurn();
        }
        BossFightState.BossState boss = fight.getBossState();
        if (boss.getBossHp() <= 0) {
            fight.getActionLogs().add("Босс повержен!");
        }
        fight.getActionLogs().add(response.toString());
        log.info("player acted");
    }

    public int finishFight(long chatId, BossFightState fight, boolean isWin) {
        provider.endFight(chatId);
        int cost = fight.getBossState().getBossType().getCost();
        if (isWin) {
            fight.getPlayers().values().stream()
                    .map(BossFightState.PlayerState::getUserId)
                    .forEach(userId -> {
                        Capybara capybara = capybaraService.getFightCapybara(chatId, userId);
                        capybara.setCurrency(capybara.getCurrency() + cost);
                        capybara.getFight().setWins(capybara.getFight().getWins() + 1);
                    });
        } else {
            fight.getPlayers().values().stream()
                    .map(BossFightState.PlayerState::getUserId)
                    .forEach(userId -> {
                        Capybara capybara = capybaraService.getFightCapybara(chatId, userId);
                        capybara.getFight().setLoses(capybara.getFight().getLoses() + 1);
                    });
        }
        return cost;
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

    private BossFightState.PlayerStats createPlayerStates(Capybara fightCapybara) {
        Integer level = fightCapybara.getLevel().getValue();
        BossFightState.PlayerStats stats = BossFightState.PlayerStats.builder()
                .hp(100 + level / 10)
                .baseDamage(100 + ((double) level / 5))
                .baseDefend(0.5)
                .baseHeal(20)
                .critChance(0.15)
                .effects(new HashSet<>())
                .build();
        Fight fight = fightCapybara.getFight();
        fight.getWeapon().apply(stats);
        fight.getShield().apply(stats);
        fight.getHeal().apply(stats);
        fight.getSpecial().apply(stats);
        return stats;
    }
}
