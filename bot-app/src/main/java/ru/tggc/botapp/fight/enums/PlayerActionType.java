package ru.tggc.botapp.fight.enums;

import lombok.Getter;
import ru.tggc.botapp.fight.BossFightState;
import ru.tggc.botapp.fight.DamageEvent;
import ru.tggc.botapp.fight.event.CriticalHitEvent;
import ru.tggc.botapp.fight.event.DamageDealtEvent;
import ru.tggc.botapp.fight.event.DefendEvent;
import ru.tggc.botapp.fight.event.HealEvent;
import ru.tggc.botapp.fight.event.PlayerActionEvent;
import ru.tggc.botapp.fight.event.PlayerActionResult;
import ru.tggc.botapp.util.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

@Getter
public enum PlayerActionType {
    ATTACK("Атака", (fight, ps) -> {
        List<PlayerActionEvent> events = new ArrayList<>();
        BossFightState.PlayerStats stats = ps.getPlayerStats();
        double damage = RandomUtils.getRandomStat(stats.getBaseDamage());
        if (RandomUtils.chance(stats.getCritChance())) {
            damage *= stats.getCritMultiplier();
            events.add(new CriticalHitEvent(ps.getUsername()));
        }
        DamageEvent damageEvent = new DamageEvent(damage);
        fight.getBossState().applyDamage(ps, damageEvent);

        events.add(new DamageDealtEvent(ps.getUsername(), damageEvent.getDamage()));
        return new PlayerActionResult(events);
    }),
    DEFEND("Защита", (_, ps) -> {
        ps.setDefending(true);
        return new PlayerActionResult(List.of(new DefendEvent(ps.getUsername())));
    }),
    HEAL("Лечение", (_, ps) -> {
        BossFightState.PlayerStats stats = ps.getPlayerStats();
        int heal = (int) RandomUtils.getRandomStat(stats.getBaseHeal());
        DamageEvent damageEvent = ps.applyHeal(heal);

        return new PlayerActionResult(List.of(new HealEvent(ps.getUsername(), damageEvent.getDamage())));
    }),
    ;

    private final String label;
    private final BiFunction<BossFightState, BossFightState.PlayerState, PlayerActionResult> function;

    PlayerActionType(String label, BiFunction<BossFightState, BossFightState.PlayerState, PlayerActionResult> function) {
        this.label = label;
        this.function = function;
    }

    public PlayerActionResult apply(BossFightState fight, BossFightState.PlayerState ps) {
        return function.apply(fight, ps);
    }
}
