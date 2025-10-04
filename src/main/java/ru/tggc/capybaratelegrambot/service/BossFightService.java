package ru.tggc.capybaratelegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.InputMediaPhoto;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageCaption;
import com.pengrad.telegrambot.request.EditMessageMedia;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.dto.UserDto;
import ru.tggc.capybaratelegrambot.domain.dto.fight.BossFightState;
import ru.tggc.capybaratelegrambot.domain.dto.fight.enums.BossAction;
import ru.tggc.capybaratelegrambot.domain.dto.fight.enums.BossType;
import ru.tggc.capybaratelegrambot.domain.dto.fight.enums.PlayerActionType;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.Fight;
import ru.tggc.capybaratelegrambot.domain.response.Response;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static ru.tggc.capybaratelegrambot.utils.Utils.ifPresent;
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

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

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
        Optional<BossFightState> optional = provider.getFight(chatId);
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
        });

        provider.startFight(chatId, fight);
        return "Бой начинается!⚔️\nBoss: " + bossType.getName() + " hp: " + bossType.getHp();
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


    public Response registerAction(CallbackQuery query, UserDto userDto, PlayerActionType action) {
        long chatId = query.maybeInaccessibleMessage().chat().id();
        return provider.getFight(chatId)
                .map(fight -> {
                    fight.getPlayers().values().stream()
                            .map(BossFightState.PlayerState::getUserId)
                            .forEach(userRateLimiterService::lock);
                    long playerId = userDto.userId();
                    BossFightState.PlayerState ps = fight.getPlayers().get(playerId);
                    if (ps == null || !ps.isAlive()) {
                        return Response.of(new SendMessage(chatId, "Твоя капибара без сознания или не участвует!"));
                    }

                    ps.setLastAction(action);
                    log.info("Игрок {} выбрал {}", ps.getUsername(), action);
                    return Response.of(new AnswerCallbackQuery(query.id()))
                            .andThen(nextTurn(query, fight)
                                    .andThen(bot -> {
                                        fight.getPlayers().values().stream()
                                                .map(BossFightState.PlayerState::getUserId)
                                                .forEach(userRateLimiterService::unlock);
                                        return CompletableFuture.completedFuture(null);
                                    }));
                })
                .orElseThrow(() -> new CapybaraException("файт не идет"));
    }

    public Response nextTurn(CallbackQuery query, BossFightState fight) {
        long chatId = query.maybeInaccessibleMessage().chat().id();
        Integer oldMessageId = query.maybeInaccessibleMessage().messageId();
        if (fight.getPlayers().values().stream()
                .filter(BossFightState.PlayerState::isAlive)
                .filter(ps -> !ps.isStunned())
                .anyMatch(p -> p.getLastAction() == null)) {
            String text = ((Message) query.maybeInaccessibleMessage())
                    .caption() + "\n==========================\n⌛ Ждём действий от всех игроков";
            EditMessageCaption message = new EditMessageCaption(chatId, oldMessageId)
                    .caption(text)
                    .replyMarkup(inlineKeyboardCreator.fightKeyboard());
            return Response.of(message);
        }

        return bot -> {
            CompletableFuture<Void> overall = new CompletableFuture<>();
            bot.execute(new DeleteMessage(chatId, oldMessageId));

            Message msg = bot.execute(new SendPhoto(chatId, "https://www.kalashnikov.ru/wp-content/uploads/2021/01/wp-image-142900476-1.jpg")
                            .caption("🐊 Босс готовится к атаке..."))
                    .message();

            int messageId = msg.messageId();

            String bossAction = doBossAction(fight);
            String playersAction = doPlayerAction(fight) +
                    "\n❤️ HP босса: " + fight.getBossState().getBossHp() + "/" + fight.getBossState().getBossType().getHp();

            List<AnimationStep> steps = getAnimationSteps(messageId, bossAction, playersAction);

            sendMessages(chatId, fight, bot, steps, overall);

            return overall;
        };
    }

    @NotNull
    private List<AnimationStep> getAnimationSteps(int messageId, String bossAction, String playersAction) {
        String result = bossAction + "\n==========================\n" + playersAction;
        return List.of(
                new AnimationStep(messageId, bossAction, "https://thumbs.dreamstime.com/b/%D0%BF%D1%80%D0%B8%D0%BC%D0%B0%D0%BD%D0%BA%D0%B0-%D0%BA%D1%80%D0%BE%D0%BA%D0%BE-%D0%B8-%D0%B0-%D0%B0%D1%82%D0%B0%D0%BA%D1%83%D1%8F-75539401.jpg"),
                new AnimationStep(messageId, playersAction,
                        "https://thumbs.dreamstime.com/b/%D0%BF%D1%80%D0%B8%D0%BC%D0%B0%D0%BD%D0%BA%D0%B0-%D0%BA%D1%80%D0%BE%D0%BA%D0%BE-%D0%B8-%D0%B0-%D0%B0%D1%82%D0%B0%D0%BA%D1%83%D1%8F-75539401.jpg"),
                new AnimationStep(messageId, result, "https://thumbs.dreamstime.com/b/%D0%BF%D1%80%D0%B8%D0%BC%D0%B0%D0%BD%D0%BA%D0%B0-%D0%BA%D1%80%D0%BE%D0%BA%D0%BE-%D0%B8-%D0%B0-%D0%B0%D1%82%D0%B0%D0%BA%D1%83%D1%8F-75539401.jpg",
                        inlineKeyboardCreator.fightKeyboard())
        );
    }

    private void sendMessages(long chatId, BossFightState fight, TelegramBot bot, List<AnimationStep> steps, CompletableFuture<Void> overall) {
        for (int i = 1; i < steps.size() + 1; i++) {
            AnimationStep step = steps.get(i - 1);
            scheduler.schedule(() -> {
                try {
                    if (step.photoPath != null) {
                        EditMessageMedia request = new EditMessageMedia(
                                chatId,
                                step.messageId,
                                new InputMediaPhoto(step.photoPath)
                                        .caption(step.text)
                        );
                        ifPresent(step.markup, request::replyMarkup);
                        bot.execute(request);
                    } else {
                        bot.execute(new EditMessageText(chatId, step.messageId, step.text));
                    }
                } catch (Exception e) {
                    overall.completeExceptionally(e);
                }
            }, i * 4L, TimeUnit.SECONDS);
        }

        scheduler.schedule(() -> {
            boolean allPlayersDead = fight.getPlayers().values().stream()
                    .noneMatch(BossFightState.PlayerState::isAlive);
            boolean bossDead = fight.getBossState().getBossHp() <= 0;

            if (allPlayersDead || bossDead) {
                String endMessage = bossDead ? "🎉 Босс повержен!" : "☠ Все капибары без сознания. Босс победил.";
                bot.execute(new SendMessage(chatId, endMessage));
                provider.endFight(chatId);
            }
            fight.getPlayers().values().forEach(ps -> ps.setLastAction(null));
            fight.setActionLogs(new ArrayList<>());
            overall.complete(null);
        }, steps.size() * 4L, TimeUnit.SECONDS);
    }

    private static String doBossAction(BossFightState fight) {
        BossAction bossAction = RandomUtils.getRandomBossAction(fight.getBossState().getBossType());
        List<BossFightState.PlayerState> alivePlayers = fight.getPlayers().values().stream()
                .filter(BossFightState.PlayerState::isAlive)
                .toList();

        if (alivePlayers.isEmpty()) return "";

        return "🐊 Ход босса:\n" +
                bossAction.apply(fight, alivePlayers) +
                checkPs(alivePlayers);
    }

    private String doPlayerAction(BossFightState fight) {
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
        response.append("==========================\n").append(getPlayersHp(players));
        return response.toString();
    }

    private String getPlayersHp(Collection<BossFightState.PlayerState> players) {
        return players.stream().map(ps -> {
            StringBuilder sb = new StringBuilder();
            sb.append("HP❤️").append(ps.getUsername()).append(": ");
            String hp = ps.getPlayerStats().getHp() >= 0 ? String.valueOf(ps.getPlayerStats().getHp()) : "0☠";
            sb.append(hp);

            return sb.toString();
        }).collect(Collectors.joining("\n"));
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

    @AllArgsConstructor
    private static class AnimationStep {
        private int messageId;
        private String text;
        private String photoPath;
        private InlineKeyboardMarkup markup;

        public AnimationStep(Integer messageId, String text, String photoPath) {
            this.messageId = messageId;
            this.text = text;
            this.photoPath = photoPath;
        }
    }
}
