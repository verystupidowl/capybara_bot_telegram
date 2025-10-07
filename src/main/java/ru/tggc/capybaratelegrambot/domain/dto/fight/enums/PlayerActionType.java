package ru.tggc.capybaratelegrambot.domain.dto.fight.enums;

import lombok.Getter;
import ru.tggc.capybaratelegrambot.domain.dto.fight.BossFightState;
import ru.tggc.capybaratelegrambot.domain.dto.fight.DamageEvent;
import ru.tggc.capybaratelegrambot.utils.RandomUtils;

import java.util.function.BiFunction;

import static ru.tggc.capybaratelegrambot.utils.Text.ATTACK_TEXTS;
import static ru.tggc.capybaratelegrambot.utils.Text.DEFEND_TEXTS;
import static ru.tggc.capybaratelegrambot.utils.Text.HEAL_TEXTS;

@Getter
public enum PlayerActionType {
    ATTACK("Атака", (fight, ps) -> {
        StringBuilder log = new StringBuilder();
        BossFightState.PlayerStats stats = ps.getPlayerStats();
        int damage = (int) RandomUtils.getRandomStat(stats.getBaseDamage());
        if (RandomUtils.chance(stats.getCritChance())) {
            log.append("\uD83D\uDCA2Критический урон!");
            damage *= 2;
        }
        DamageEvent damageEvent = new DamageEvent(damage);
        fight.getBossState().applyDamage(ps, damageEvent);

        String text = String.format(
                RandomUtils.getRandomFromList(ATTACK_TEXTS),
                ps.getUsername(), damageEvent.getDamage()
        );
        return log.append(text).toString();
    }),
    DEFEND("Защита", (fight, ps) -> {
        ps.setDefending(true);
        return String.format(RandomUtils.getRandomFromList(DEFEND_TEXTS), ps.getUsername());
    }),
    HEAL("Лечение", (fight, ps) -> {
        BossFightState.PlayerStats stats = ps.getPlayerStats();
        int heal = (int) RandomUtils.getRandomStat(stats.getBaseHeal());
        DamageEvent damageEvent = ps.applyHeal(heal);

        return String.format(
                RandomUtils.getRandomFromList(HEAL_TEXTS),
                ps.getUsername(), damageEvent.getDamage()
        );
    }),
    ;

    private final String label;
    private final BiFunction<BossFightState, BossFightState.PlayerState, String> function;

    PlayerActionType(String label, BiFunction<BossFightState, BossFightState.PlayerState, String> function) {
        this.label = label;
        this.function = function;
    }

    public String apply(BossFightState fight, BossFightState.PlayerState ps) {
        return function.apply(fight, ps);
    }
}
