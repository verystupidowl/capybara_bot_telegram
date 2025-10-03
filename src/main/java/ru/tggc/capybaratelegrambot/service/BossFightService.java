package ru.tggc.capybaratelegrambot.service;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.InputMediaPhoto;
import com.pengrad.telegrambot.request.EditMessageMedia;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.domain.dto.BossFightState;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.dto.UserDto;
import ru.tggc.capybaratelegrambot.domain.dto.enums.BossAction;
import ru.tggc.capybaratelegrambot.domain.dto.enums.BossType;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.Fight;
import ru.tggc.capybaratelegrambot.domain.response.Response;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.provider.BossFightProvider;
import ru.tggc.capybaratelegrambot.utils.RandomUtils;
import ru.tggc.capybaratelegrambot.utils.UserRateLimiterService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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

    private static final List<String> ATTACK_TEXTS = List.of(
            "⚔️ %s прыгнул на босса и вцепился зубами! Урон: %d",
            "💥 %s с размаху ударил хвостом по боссу! Урон: %d",
            "🔥 %s атакует со всей силы! Урон: %d"
    );
    private static final List<String> DEFEND_TEXTS = List.of(
            "🛡️ %s встал в оборону и приготовился к удару",
            "🌊 %s прячется за камышами и снижает входящий урон",
            "🪵 %s нашёл бревно и использует его как щит"
    );
    private static final List<String> HEAL_TEXTS = List.of(
            "🌿 %s жует свежую травку и восстанавливает %d HP",
            "💧 %s сделал глоток прохладной воды и восстановил %d HP",
            "✨ %s вдохнул силы природы и восстановил %d HP"
    );

    public void joinFight(CapybaraContext ctx, String username) {
        Capybara capybara = capybaraService.getFightCapybara(ctx.chatId(), ctx.userId());
        throwIf(!capybara.getFight().getFightAction().canPerform(), () -> new CapybaraException("u will can join only in " + timedActionService.getStatus(capybara.getFight().getFightAction())));
        provider.joinFight(ctx.chatId(), ctx.userId(), username);
    }

    public void leaveFight(Long chatId, Long userId) {
        provider.leaveFight(chatId, userId);
    }

    public String startFight(Long chatId) {
        Optional<BossFightState> optional = provider.getFight(chatId);
        throwIf(optional.isPresent(), () -> new CapybaraException("Файт уже идет"));
        BossFightState fight = new BossFightState();
        BossType boss = RandomUtils.geetRandomBoss();
        fight.setBoss(boss);
        fight.setBossHp(boss.getHp());
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
                    .build();
            fight.getPlayers().put(user.userId(), ps);
        });

        provider.startFight(chatId, fight);
        return "fight started!\nBoss: " + boss.getName() + " hp: " + boss.getHp();
    }

    private BossFightState.PlayerStats createPlayerStates(Capybara fightCapybara) {
        Integer level = fightCapybara.getLevel().getValue();
        BossFightState.PlayerStats stats = BossFightState.PlayerStats.builder()
                .hp(100 + level / 10)
                .baseDamage(100 + ((double) level / 5))
                .baseDefend(50 + ((double) level / 5))
                .baseHeal(20 + ((double) level / 5))
                .critChance(15 + (double) level / 5)
                .vampirism(0)
                .build();
        Fight fight = fightCapybara.getFight();
        fight.getWeapon().apply(stats);
        fight.getShield().apply(stats);
        fight.getHeal().apply(stats);
        fight.getSpecial().apply(stats);
        return stats;
    }


    public Response registerAction(long chatId, UserDto userDto, BossFightState.ActionType action) {
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
                    return nextTurn(chatId, fight)
                            .andThen(bot -> {
                                fight.getPlayers().values().stream()
                                        .map(BossFightState.PlayerState::getUserId)
                                        .forEach(userRateLimiterService::unlock);
                                return CompletableFuture.completedFuture(null);
                            });
                })
                .orElseThrow(() -> new CapybaraException("файт не идет"));
    }

    public Response nextTurn(long chatId, BossFightState fight) {
        if (fight.getPlayers().values().stream()
                .filter(BossFightState.PlayerState::isAlive)
                .filter(ps -> !ps.isStunned())
                .anyMatch(p -> p.getLastAction() == null)) {
            return Response.of(new SendMessage(chatId, "⌛ Ждём действий от всех игроков"));
        }

        return bot -> {
            CompletableFuture<Void> overall = new CompletableFuture<>();

            Message msg = bot.execute(new SendPhoto(chatId, "https://www.kalashnikov.ru/wp-content/uploads/2021/01/wp-image-142900476-1.jpg")
                            .caption("🐊 Босс готовится к атаке..."))
                    .message();

            int messageId = msg.messageId();

            String bossAction = doBossAction(fight);
            String playersAction = doPlayerAction(fight);

            List<AnimationStep> steps = List.of(
                    new AnimationStep(messageId, bossAction, "https://thumbs.dreamstime.com/b/%D0%BF%D1%80%D0%B8%D0%BC%D0%B0%D0%BD%D0%BA%D0%B0-%D0%BA%D1%80%D0%BE%D0%BA%D0%BE-%D0%B8-%D0%B0-%D0%B0%D1%82%D0%B0%D0%BA%D1%83%D1%8F-75539401.jpg"),
                    new AnimationStep(messageId, playersAction +
                            "\n❤️ HP босса: " + fight.getBossHp() + "/" + fight.getBoss().getHp(),
                            "https://thumbs.dreamstime.com/b/%D0%BF%D1%80%D0%B8%D0%BC%D0%B0%D0%BD%D0%BA%D0%B0-%D0%BA%D1%80%D0%BE%D0%BA%D0%BE-%D0%B8-%D0%B0-%D0%B0%D1%82%D0%B0%D0%BA%D1%83%D1%8F-75539401.jpg"),
                    new AnimationStep(messageId, generateStatus(fight), "https://news.store.rambler.ru/img/a2176d6eec9bdb79276b517d10d3c930?img-format=auto&img-1-resize=height:400,fit:max&img-2-filter=sharpen", inlineKeyboardCreator.fightKeyboard())
            );

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
                }, i * 4, TimeUnit.SECONDS);
            }

            scheduler.schedule(() -> {
                boolean allPlayersDead = fight.getPlayers().values().stream()
                        .noneMatch(BossFightState.PlayerState::isAlive);
                boolean bossDead = fight.getBossHp() <= 0;

                if (allPlayersDead || bossDead) {
                    String endMessage = bossDead ? "🎉 Босс повержен!" : "☠ Все капибары без сознания. Босс победил.";
                    bot.execute(new SendMessage(chatId, endMessage));
                    provider.endFight(chatId);
                }
                fight.getPlayers().values().forEach(ps -> ps.setLastAction(null));
                fight.setActionLogs(new ArrayList<>());
                overall.complete(null);
            }, steps.size() * 4, TimeUnit.SECONDS);

            return overall;
        };
    }

    private static String doBossAction(BossFightState fight) {
        BossAction bossAction = RandomUtils.getRandomBossAction(fight.getBoss());
        List<BossFightState.PlayerState> alivePlayers = fight.getPlayers().values().stream()
                .filter(BossFightState.PlayerState::isAlive)
                .toList();

        if (alivePlayers.isEmpty()) return "";


        StringBuilder log = new StringBuilder("🐊 Ход босса: " + bossAction.name() + "\n\n");
        int value = 0;
        StringBuilder username = new StringBuilder();
        switch (bossAction) {
            case TAIL_ON_THE_WATER -> {
                username = new StringBuilder("всех");
                for (BossFightState.PlayerState ps : alivePlayers) {
                    value = BossAction.TAIL_ON_THE_WATER.getDamage();
                    if (ps.isDefending()) {
                        value *= (int) (RandomUtils.getRandomStat(ps.getPlayerStats().getBaseDefend()) / 100);
                    }
                    ps.getPlayerStats().setHp(ps.getPlayerStats().getHp() - value);
                    log.append("🌊 ").append(ps.getUsername())
                            .append(" получил ").append(value)
                            .append(" урона (HP: ").append(Math.max(ps.getPlayerStats().getHp(), 0)).append(")\n");
                }
            }
            case BITE -> {
                BossFightState.PlayerState ps = RandomUtils.getRandomFromList(alivePlayers);
                username = new StringBuilder(ps.getUsername());
                value = BossAction.BITE.getDamage();
                if (ps.isDefending()) {
                    value *= (int) (RandomUtils.getRandomStat(ps.getPlayerStats().getBaseDefend()) / 100);
                }
                ps.getPlayerStats().setHp(ps.getPlayerStats().getHp() - value);
                log.append("🦷 Босс укусил ").append(ps.getUsername())
                        .append(" на ").append(value)
                        .append(" урона (HP: ").append(Math.max(ps.getPlayerStats().getHp(), 0)).append(")\n");
            }
            case STUN -> {
                BossFightState.PlayerState ps = RandomUtils.getRandomFromList(alivePlayers);
                username = new StringBuilder(ps.getUsername());
                value = BossAction.STUN.getDamage();
                if (ps.isDefending()) {
                    value *= (int) (RandomUtils.getRandomStat(ps.getPlayerStats().getBaseDefend()) / 100);
                }
                ps.getPlayerStats().setHp(ps.getPlayerStats().getHp() - value);
                ps.setStunned(true);
                log.append("💥 Босс застанил ").append(ps.getUsername())
                        .append(" (HP: ").append(Math.max(ps.getPlayerStats().getHp(), 0)).append(")\n");
            }
            case AOE_DAMAGE -> {
                log.append("🌊 Босс поднял волну и ударил всех!\n");
                username = new StringBuilder("всех");
                for (BossFightState.PlayerState ps : alivePlayers) {
                    value = BossAction.AOE_DAMAGE.getDamage();
                    if (ps.isDefending()) {
                        value *= (int) (RandomUtils.getRandomStat(ps.getPlayerStats().getBaseDefend()) / 100);
                    }
                    ps.getPlayerStats().setHp(ps.getPlayerStats().getHp() - value);
                    log.append(" └ ").append(ps.getUsername())
                            .append(" получил ").append(value)
                            .append(" урона (HP: ").append(Math.max(ps.getPlayerStats().getHp(), 0)).append(")\n");
                }
            }
            case AOE_STUN -> {
                log.append("⚡ Босс издал рёв, сотрясая землю!\n");
                for (BossFightState.PlayerState ps : alivePlayers) {
                    if (RandomUtils.chance(0.5)) {
                        username.append(ps.getUsername());
                        value = BossAction.AOE_STUN.getDamage();
                        if (ps.isDefending()) {
                            value *= (int) (RandomUtils.getRandomStat(ps.getPlayerStats().getBaseDefend()) / 100);
                        }
                        ps.getPlayerStats().setHp(ps.getPlayerStats().getHp() - value);
                        ps.setStunned(true);
                        log.append(" └ 😵 ").append(ps.getUsername())
                                .append(" оглушён и получил ").append(value)
                                .append(" урона (HP: ").append(Math.max(ps.getPlayerStats().getHp(), 0)).append(")\n");
                    }
                }
            }
            case FOCUSED_STRIKE -> {
                BossFightState.PlayerState ps = RandomUtils.getRandomFromList(alivePlayers);
                username.append(ps.getUsername());
                value = BossAction.FOCUSED_STRIKE.getDamage();
                if (RandomUtils.chance(0.2)) {
                    value *= 2;
                }
                if (ps.isDefending()) {
                    value *= (int) (RandomUtils.getRandomStat(ps.getPlayerStats().getBaseDefend()) / 100);
                }
                ps.getPlayerStats().setHp(ps.getPlayerStats().getHp() - value);
                log.append("💢 Босс нанёс мощный удар по ").append(ps.getUsername())
                        .append(" на ").append(value)
                        .append(" урона (HP: ").append(Math.max(ps.getPlayerStats().getHp(), 0)).append(")\n");
            }
            case HEAL -> {
                username.append("себя");
                value = Math.abs(BossAction.HEAL.getDamage());
                fight.setBossHp(Math.min(fight.getBossHp() + value, fight.getBoss().getHp()));
                log.append("🩸 Босс втянул силы из земли и восстановил ").append(value)
                        .append(" HP (").append(fight.getBossHp()).append("/").append(fight.getBoss().getHp()).append(")\n");
            }
        }
        fight.getActionLogs().add(new BossFightState.ActionLog("Boss", bossAction.name(), value, username.toString()));
        return log.append(checkPs(alivePlayers)).toString();
    }

    private String doPlayerAction(BossFightState fight) {
        StringBuilder response = new StringBuilder();
        Collection<BossFightState.PlayerState> players = fight.getPlayers().values();
        List<BossFightState.ActionLog> logs = new ArrayList<>();

        for (BossFightState.PlayerState ps : players) {
            if (!ps.isAlive()) {
                continue;
            }
            if (ps.isStunned()) {
                response.append("😵 ").append(ps.getUsername())
                        .append(" оглушён и пропускает ход!\n");
                ps.setStunned(false);
                continue;
            }

            BossFightState.ActionLog log = new BossFightState.ActionLog();
            log.setActor(ps.getUsername());
            log.setWhom("босса");
            BossFightState.PlayerStats stats = ps.getPlayerStats();

            switch (ps.getLastAction()) {
                case ATTACK -> {
                    int damage = (int) RandomUtils.getRandomStat(stats.getBaseDamage());
                    if (RandomUtils.chance(stats.getCritChance() / 100)) {
                        damage *= 2;
                    }
                    fight.setBossHp(fight.getBossHp() - damage);

                    String text = String.format(
                            RandomUtils.getRandomFromList(ATTACK_TEXTS),
                            ps.getUsername(), damage
                    );
                    response.append(text);
                    log.setAction(BossFightState.ActionType.ATTACK.name());
                    log.setValue(damage);
                }
                case DEFEND -> {
                    ps.setDefending(true);
                    String text = String.format(RandomUtils.getRandomFromList(DEFEND_TEXTS), ps.getUsername());
                    response.append(text);
                    log.setAction(BossFightState.ActionType.DEFEND.name());
                    log.setValue(2);
                }
                case HEAL -> {
                    int heal = (int) RandomUtils.getRandomStat(stats.getBaseHeal());
                    ps.getPlayerStats().setHp(ps.getPlayerStats().getHp() + heal);

                    String text = String.format(
                            RandomUtils.getRandomFromList(HEAL_TEXTS),
                            ps.getUsername(), heal
                    );
                    response.append(text);
                    log.setAction(BossFightState.ActionType.HEAL.name());
                    log.setValue(heal);
                }
            }
            logs.add(log);
        }
        fight.getActionLogs().addAll(logs);

        return response.toString();
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

    private String generateStatus(BossFightState fight) {
        List<BossFightState.ActionLog> logs = fight.getActionLogs();
        StringBuilder sb = new StringBuilder("📊 Статус после хода:\n");
        sb.append("🐊 Босс: ").append(fight.getBossHp()).append("/").append(fight.getBoss().getHp()).append(" HP\n");
        fight.getPlayers().values().forEach(p ->
                sb.append("🧑‍🦱 ").append(p.getUsername())
                        .append(": ").append(Math.max(p.getPlayerStats().getHp(), 0)).append(" HP")
                        .append(p.isAlive() ? "" : " ☠")
                        .append("\n")
        );
        sb.append("\n");
        for (BossFightState.ActionLog logEntry : logs) {
            sb.append(logEntry.getActor())
                    .append(" сделал ")
                    .append(logEntry.getAction())
                    .append(": ")
                    .append(logEntry.getValue())
                    .append(" ")
                    .append(logEntry.getWhom())
                    .append("\n");
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
