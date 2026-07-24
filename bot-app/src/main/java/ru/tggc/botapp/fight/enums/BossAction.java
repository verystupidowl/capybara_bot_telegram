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
import ru.tggc.botapp.fight.event.boss.BossDamageDealtEvent;
import ru.tggc.botapp.fight.event.boss.FocusedStrikeEvent;
import ru.tggc.botapp.fight.event.boss.BossHealEvent;
import ru.tggc.botapp.fight.event.boss.PoisonBiteEvent;
import ru.tggc.botapp.fight.event.boss.StunEvent;
import ru.tggc.botapp.fight.event.boss.TailOnTheWaterEvent;
import ru.tggc.botapp.fight.event.boss.TailSlamDustEvent;
import ru.tggc.botapp.util.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Getter
public enum BossAction {
    TAIL_ON_THE_WATER((alivePlayers) -> {
        List<BossActionEvent> events = new ArrayList<>();
        events.add(new TailOnTheWaterEvent());
        alivePlayers.forEach(ps -> {
            int damage = RandomUtils.getRandomInt(30) + 10;
            ps.applyDamage(damage);
            events.add(new BossDamageDealtEvent(ps.getUsername(), damage));
        });
        return new BossActionResult(events);
    }),
    BITE((alivePlayers) -> {
        BossFightState.PlayerState ps = RandomUtils.getRandomFromList(alivePlayers);
        int damage = RandomUtils.getRandomInt(30) + 50;
        ps.applyDamage(damage);
        return new BossActionResult(List.of(new BiteEvent(ps.getUsername(), damage)));
    }),
    STUN((alivePlayers) -> {
        BossFightState.PlayerState ps = RandomUtils.getRandomFromList(alivePlayers);
        int damage = RandomUtils.getRandomInt(30) + 1;
        ps.applyDamage(damage);
        ps.getPlayerStats().getEffects().add(new StunEffect(1));
        return new BossActionResult(List.of(new StunEvent(ps.getUsername(), damage)));
    }),
    AOE_DAMAGE((alivePlayers) -> {
        List<BossActionEvent> events = new ArrayList<>();
        events.add(new AoeDamageEvent());
        alivePlayers.forEach(ps -> {
            int damage = RandomUtils.getRandomInt(10) + 10;
            ps.applyDamage(damage);
            events.add(new BossDamageDealtEvent(ps.getUsername(), damage));
        });
        return new BossActionResult(events);
    }),
    AOE_STUN((alivePlayers) -> {
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
    HEAL((_) -> {
        int heal = RandomUtils.getRandomInt(30) + 10;
        return new BossActionResult(List.of(new BossHealEvent(heal)));
    }),
    FOCUSED_STRIKE((alivePlayers) -> {
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
    POISON_BITE((alivePlayers) -> {
        BossFightState.PlayerState ps = RandomUtils.getRandomFromList(alivePlayers);
        int damage = RandomUtils.getRandomInt(50) + 10;
        ps.getPlayerStats().getEffects().add(new PoisonEffect());
        ps.applyDamage(damage);
        return new BossActionResult(List.of(new PoisonBiteEvent(ps.getUsername(), damage)));
    }),
    TAIL_SLAM_DUST((alivePlayers) -> {
        BossFightState.PlayerState ps = RandomUtils.getRandomFromList(alivePlayers);
        int damage = RandomUtils.getRandomInt(10) + 5;
        ps.getPlayerStats().getEffects().add(new BlindnessEffect(0.25, 10));
        ps.applyDamage(damage);
        return new BossActionResult(List.of(new TailSlamDustEvent(ps.getUsername(), damage, 10)));
    }),
    TAIL_MUD_SPLASH((alivePlayers) -> {
        int damage = RandomUtils.getRandomInt(10) + 5;
        List<BossActionEvent> events = new ArrayList<>();
        alivePlayers.forEach(ps -> {
            ps.getPlayerStats().getEffects().add(new WeakenedEffect(0.5, 3));
            ps.applyDamage(damage);
            events.add(new BossDamageDealtEvent(ps.getUsername(), damage));
        });
        return new BossActionResult(events);
    });

    private final Function<List<BossFightState.PlayerState>, BossActionResult> function;

    BossAction(Function<List<BossFightState.PlayerState>, BossActionResult> function) {
        this.function = function;
    }

    public BossActionResult apply(List<BossFightState.PlayerState> alivePlayers) {
        return function.apply(alivePlayers);
    }
}
