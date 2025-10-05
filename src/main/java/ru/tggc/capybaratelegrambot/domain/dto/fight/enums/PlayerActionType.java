package ru.tggc.capybaratelegrambot.domain.dto.fight.enums;

import ru.tggc.capybaratelegrambot.domain.dto.fight.BossFightState;
import ru.tggc.capybaratelegrambot.domain.dto.fight.DamageEvent;
import ru.tggc.capybaratelegrambot.utils.RandomUtils;

import java.util.function.BiFunction;

import static ru.tggc.capybaratelegrambot.service.BossFightService.ATTACK_TEXTS;
import static ru.tggc.capybaratelegrambot.service.BossFightService.DEFEND_TEXTS;
import static ru.tggc.capybaratelegrambot.service.BossFightService.HEAL_TEXTS;

public enum PlayerActionType {
    ATTACK((fight, ps) -> {
        StringBuilder log = new StringBuilder();
        BossFightState.PlayerStats stats = ps.getPlayerStats();
        int damage = (int) RandomUtils.getRandomStat(stats.getBaseDamage());
        if (RandomUtils.chance(stats.getCritChance())) {
            log.append("\uD83D\uDCA2Критический урон!").append("\n");
            damage *= 2;
        }
        DamageEvent damageEvent = new DamageEvent(damage);
        fight.getBossState().applyDamage(ps, damageEvent);

        String text = String.format(
                RandomUtils.getRandomFromList(ATTACK_TEXTS),
                ps.getUsername(), damage
        );
        return log.append(text).append("\n").toString();
    }),
    DEFEND((fight, ps) -> {
        ps.setDefending(true);
        return String.format(RandomUtils.getRandomFromList(DEFEND_TEXTS), ps.getUsername()) + "\n";
    }),
    HEAL((fight, ps) -> {
        BossFightState.PlayerStats stats = ps.getPlayerStats();
        int heal = (int) RandomUtils.getRandomStat(stats.getBaseHeal());
        ps.applyHeal(heal);

        String text = String.format(
                RandomUtils.getRandomFromList(HEAL_TEXTS),
                ps.getUsername(), heal
        );
        return text + "\n";
    }),
    ;

    private final BiFunction<BossFightState, BossFightState.PlayerState, String> function;

    PlayerActionType(BiFunction<BossFightState, BossFightState.PlayerState, String> function) {
        this.function = function;
    }

    public String apply(BossFightState fight, BossFightState.PlayerState ps) {
        return function.apply(fight, ps);
    }
}
