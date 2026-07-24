package ru.tggc.botapp.fight.enums;

import lombok.Getter;
import ru.tggc.botapp.fight.BossFightState;
import ru.tggc.botapp.fight.DamageEvent;
import ru.tggc.botapp.fight.event.player.PlayerCriticalHitEvent;
import ru.tggc.botapp.fight.event.player.PlayerDamageDealtEvent;
import ru.tggc.botapp.fight.event.player.PlayerDefendEvent;
import ru.tggc.botapp.fight.event.player.PlayerHealEvent;
import ru.tggc.botapp.fight.event.player.PlayerActionEvent;
import ru.tggc.botapp.fight.event.player.PlayerActionResult;
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
            events.add(new PlayerCriticalHitEvent(ps.getUsername()));
        }
        DamageEvent damageEvent = new DamageEvent(damage);
        fight.getBossState().applyDamage(ps, damageEvent);

        events.add(new PlayerDamageDealtEvent(ps.getUsername(), damageEvent.getDamage()));
        return new PlayerActionResult(events);
    }),
    DEFEND("Защита", (_, ps) -> {
        ps.setDefending(true);
        return new PlayerActionResult(List.of(new PlayerDefendEvent(ps.getUsername())));
    }),
    HEAL("Лечение", (_, ps) -> {
        BossFightState.PlayerStats stats = ps.getPlayerStats();
        int heal = (int) RandomUtils.getRandomStat(stats.getBaseHeal());
        DamageEvent damageEvent = ps.applyHeal(heal);

        return new PlayerActionResult(List.of(new PlayerHealEvent(ps.getUsername(), damageEvent.getDamage())));
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
