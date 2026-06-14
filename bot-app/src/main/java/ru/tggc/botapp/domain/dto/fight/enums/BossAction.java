package ru.tggc.botapp.domain.dto.fight.enums;

import lombok.Getter;
import ru.tggc.botapp.domain.dto.fight.BossFightState;
import ru.tggc.botapp.domain.dto.fight.DamageEvent;
import ru.tggc.botapp.domain.dto.fight.effect.negative.BlindnessEffect;
import ru.tggc.botapp.domain.dto.fight.effect.negative.PoisonEffect;
import ru.tggc.botapp.domain.dto.fight.effect.negative.StunEffect;
import ru.tggc.botapp.domain.dto.fight.effect.negative.WeakenedEffect;
import ru.tggc.botapp.util.RandomUtils;

import java.util.List;
import java.util.function.BiFunction;

@Getter
public enum BossAction {
    TAIL_ON_THE_WATER((_, alivePlayers) -> {
        StringBuilder log = new StringBuilder("Босс бьёт по воде хвостом!🌊");
        alivePlayers.forEach(ps -> {
            int damage = RandomUtils.getRandomInt(30) + 10;
            DamageEvent damageEvent = ps.applyDamage(damage);
            log.append("🌊 ").append(ps.getUsername())
                    .append(" получил ").append(damageEvent.getDamage())
                    .append(" урона");
        });
        return log.toString();
    }),
    BITE((_, alivePlayers) -> {
        BossFightState.PlayerState ps = RandomUtils.getRandomFromList(alivePlayers);
        int damage = RandomUtils.getRandomInt(30) + 50;
        DamageEvent damageEvent = ps.applyDamage(damage);
        return "🦷 Босс укусил " + ps.getUsername() +
                " на " + damageEvent.getDamage() +
                " урона";
    }),
    STUN((_, alivePlayers) -> {
        BossFightState.PlayerState ps = RandomUtils.getRandomFromList(alivePlayers);
        int damage = RandomUtils.getRandomInt(30) + 1;
        DamageEvent damageEvent = ps.applyDamage(damage);
        ps.getPlayerStats().getEffects().add(new StunEffect(1));
        return "💥 Босс застанил " + ps.getUsername() + " на " + damageEvent.getDamage();
    }),
    AOE_DAMAGE((_, alivePlayers) -> {
        StringBuilder log = new StringBuilder("🌊 Босс поднял волну и ударил всех!\n");
        alivePlayers.forEach(ps -> {
            int damage = RandomUtils.getRandomInt(10) + 10;
            DamageEvent damageEvent = ps.applyDamage(damage);
            log.append(" └ ").append(ps.getUsername())
                    .append(" получил ").append(damageEvent.getDamage())
                    .append(" урона");
        });
        return log.toString();
    }),
    AOE_STUN((_, alivePlayers) -> {
        StringBuilder log = new StringBuilder("⚡ Босс издал рёв, сотрясая землю!\n");
        alivePlayers.stream()
                .filter(_ -> RandomUtils.chance(0.5))
                .forEach(ps -> {
                    int damage = RandomUtils.getRandomInt(10);
                    DamageEvent damageEvent = ps.applyDamage(damage);
                    log.append(" └ 😵 ").append(ps.getUsername())
                            .append(" оглушён и получил ").append(damageEvent.getDamage())
                            .append(" урона");
                });
        return log.toString();
    }),
    HEAL((_, _) -> {
        int heal = RandomUtils.getRandomInt(30) + 10;
        return "🩸 Босс втянул силы из земли и восстановил " + heal;
    }),
    FOCUSED_STRIKE((_, alivePlayers) -> {
        StringBuilder log = new StringBuilder();
        BossFightState.PlayerState ps = RandomUtils.getRandomFromList(alivePlayers);
        int damage = RandomUtils.getRandomInt(30) + 20;
        if (RandomUtils.chance(0.2)) {
            log.append("Критический урон!");
            damage *= 2;
        }
        DamageEvent damageEvent = ps.applyDamage(damage);
        return log.append("💢 Босс нанёс мощный удар по ").append(ps.getUsername())
                .append(" на ").append(damageEvent.getDamage())
                .append(" урона")
                .toString();
    }),
    POISON_BITE((_, alivePlayers) -> {
        BossFightState.PlayerState ps = RandomUtils.getRandomFromList(alivePlayers);
        int damage = RandomUtils.getRandomInt(50) + 10;
        ps.getPlayerStats().getEffects().add(new PoisonEffect());
        DamageEvent damageEvent = ps.applyDamage(damage);
        return "\uD83D\uDCA6 Босс сделал ядовитый укус! по " + ps.getUsername() +
                " на " + damageEvent.getDamage() + "! Теперь ему нужно противоядие\uD83E\uDDEA";
    }),
    TAIL_SLAM_DUST((_, alivePlayers) -> {
        BossFightState.PlayerState ps = RandomUtils.getRandomFromList(alivePlayers);
        int damage = RandomUtils.getRandomInt(10) + 5;
        ps.getPlayerStats().getEffects().add(new BlindnessEffect(0.25, 10));
        DamageEvent damageEvent = ps.applyDamage(damage);
        return "Босс бьёт хвостом по земле, поднимая облако пыли. Она ослепляет " +
                ps.getUsername() + ", заставляя его промахиваться и наносит ему " +
                damageEvent.getDamage() + " урона! Капибаре нужно 10 ходов, чтобы оправиться";
    }),
    TAIL_MUD_SPLASH((_, alivePlayers) -> {
        StringBuilder log = new StringBuilder("Босс бьет хвостом по грязи и заливает всем капибарам глаза\n")
                .append("Им нужно 3 хода, чтобы оправиться, а пока их урон уменьшен вдвое!");
        int damage = RandomUtils.getRandomInt(10) + 5;
        alivePlayers.forEach(ps -> {
            ps.getPlayerStats().getEffects().add(new WeakenedEffect(0.5, 3));
            DamageEvent damageEvent = ps.applyDamage(damage);
            log.append(" └ ").append(ps.getUsername())
                    .append(" получил ").append(damageEvent.getDamage())
                    .append(" урона");
        });
        return log.toString();
    });

    private final BiFunction<BossFightState, List<BossFightState.PlayerState>, String> function;

    BossAction(BiFunction<BossFightState, List<BossFightState.PlayerState>, String> function) {
        this.function = function;
    }

    public String apply(BossFightState fight, List<BossFightState.PlayerState> alivePlayers) {
        return function.apply(fight, alivePlayers);
    }
}
