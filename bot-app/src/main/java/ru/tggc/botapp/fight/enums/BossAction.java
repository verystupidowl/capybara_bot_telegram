package ru.tggc.botapp.fight.enums;

import lombok.Getter;
import ru.tggc.botapp.fight.BossFightState;
import ru.tggc.botapp.fight.effect.negative.BlindnessEffect;
import ru.tggc.botapp.fight.effect.negative.PoisonEffect;
import ru.tggc.botapp.fight.effect.negative.StunEffect;
import ru.tggc.botapp.fight.effect.negative.WeakenedEffect;
import ru.tggc.botapp.fight.event.boss.AoeDamageEvent;
import ru.tggc.botapp.fight.event.boss.AoeStunEvent;
import ru.tggc.botapp.fight.event.boss.BiteEvent;
import ru.tggc.botapp.fight.event.boss.BossActionEvent;
import ru.tggc.botapp.fight.event.boss.BossActionResult;
import ru.tggc.botapp.fight.event.boss.BossCriticalHitEvent;
import ru.tggc.botapp.fight.event.boss.DamageDealtEvent;
import ru.tggc.botapp.fight.event.boss.FocusedStrikeEvent;
import ru.tggc.botapp.fight.event.boss.HealEvent;
import ru.tggc.botapp.fight.event.boss.PoisonBiteEvent;
import ru.tggc.botapp.fight.event.boss.StunEvent;
import ru.tggc.botapp.fight.event.boss.TailOnTheWaterEvent;
import ru.tggc.botapp.fight.event.boss.TailSlamDustEvent;
import ru.tggc.botapp.util.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

@Getter
public enum BossAction {
    TAIL_ON_THE_WATER((_, alivePlayers) -> {
        List<BossActionEvent> events = new ArrayList<>();
        events.add(new TailOnTheWaterEvent());
        alivePlayers.forEach(ps -> {
            int damage = RandomUtils.getRandomInt(30) + 10;
            ps.applyDamage(damage);
            events.add(new DamageDealtEvent(ps.getUsername(), damage));
        });
        return new BossActionResult(events);
    }),
    BITE((_, alivePlayers) -> {
        BossFightState.PlayerState ps = RandomUtils.getRandomFromList(alivePlayers);
        int damage = RandomUtils.getRandomInt(30) + 50;
        ps.applyDamage(damage);
        return new BossActionResult(List.of(new BiteEvent(ps.getUsername(), damage)));
    }),
    STUN((_, alivePlayers) -> {
        BossFightState.PlayerState ps = RandomUtils.getRandomFromList(alivePlayers);
        int damage = RandomUtils.getRandomInt(30) + 1;
        ps.applyDamage(damage);
        ps.getPlayerStats().getEffects().add(new StunEffect(1));
        return new BossActionResult(List.of(new StunEvent(ps.getUsername(), damage)));
    }),
    AOE_DAMAGE((_, alivePlayers) -> {
        List<BossActionEvent> events = new ArrayList<>();
        events.add(new AoeDamageEvent());
        alivePlayers.forEach(ps -> {
            int damage = RandomUtils.getRandomInt(10) + 10;
            ps.applyDamage(damage);
            events.add(new DamageDealtEvent(ps.getUsername(), damage));
        });
        return new BossActionResult(events);
    }),
    AOE_STUN((_, alivePlayers) -> {
        List<BossActionEvent> events = new ArrayList<>();
        events.add(new AoeStunEvent());
        alivePlayers.stream()
                .filter(_ -> RandomUtils.chance(0.5))
                .forEach(ps -> {
                    int damage = RandomUtils.getRandomInt(10);
                    ps.applyDamage(damage);
                    events.add(new StunEvent(ps.getUsername(), damage));
                });
        return new BossActionResult(events);
    }),
    HEAL((_, _) -> {
        int heal = RandomUtils.getRandomInt(30) + 10;
        return new BossActionResult(List.of(new HealEvent(heal)));
    }),
    FOCUSED_STRIKE((_, alivePlayers) -> {
        List<BossActionEvent> events = new ArrayList<>();
        BossFightState.PlayerState ps = RandomUtils.getRandomFromList(alivePlayers);
        int damage = RandomUtils.getRandomInt(30) + 20;
        if (RandomUtils.chance(0.2)) {
            events.add(new BossCriticalHitEvent());
            damage *= 2;
        }
        ps.applyDamage(damage);
        events.add(new FocusedStrikeEvent(ps.getUsername(), damage));
        return new BossActionResult(events);
    }),
    POISON_BITE((_, alivePlayers) -> {
        BossFightState.PlayerState ps = RandomUtils.getRandomFromList(alivePlayers);
        int damage = RandomUtils.getRandomInt(50) + 10;
        ps.getPlayerStats().getEffects().add(new PoisonEffect());
        ps.applyDamage(damage);
        return new BossActionResult(List.of(new PoisonBiteEvent(ps.getUsername(), damage)));
    }),
    TAIL_SLAM_DUST((_, alivePlayers) -> {
        BossFightState.PlayerState ps = RandomUtils.getRandomFromList(alivePlayers);
        int damage = RandomUtils.getRandomInt(10) + 5;
        ps.getPlayerStats().getEffects().add(new BlindnessEffect(0.25, 10));
        ps.applyDamage(damage);
        return new BossActionResult(List.of(new TailSlamDustEvent(ps.getUsername(), damage, 10)));
    }),
    TAIL_MUD_SPLASH((_, alivePlayers) -> {
        int damage = RandomUtils.getRandomInt(10) + 5;
        List<BossActionEvent> events = new ArrayList<>();
        alivePlayers.forEach(ps -> {
            ps.getPlayerStats().getEffects().add(new WeakenedEffect(0.5, 3));
            ps.applyDamage(damage);
            events.add(new DamageDealtEvent(ps.getUsername(), damage));
        });
        return new BossActionResult(events);
    });

    private final BiFunction<BossFightState, List<BossFightState.PlayerState>, BossActionResult> function;

    BossAction(BiFunction<BossFightState, List<BossFightState.PlayerState>, BossActionResult> function) {
        this.function = function;
    }

    public BossActionResult apply(BossFightState fight, List<BossFightState.PlayerState> alivePlayers) {
        return function.apply(fight, alivePlayers);
    }
}
