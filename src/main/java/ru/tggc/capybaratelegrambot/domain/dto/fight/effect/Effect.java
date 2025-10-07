package ru.tggc.capybaratelegrambot.domain.dto.fight.effect;

import ru.tggc.capybaratelegrambot.domain.dto.fight.BossFightState;
import ru.tggc.capybaratelegrambot.domain.dto.fight.DamageEvent;

public interface Effect {

    default void onDamageTaken(BossFightState.PlayerState ps, DamageEvent damage) {
    }

    default void onDamageGiven(BossFightState.PlayerState ps, DamageEvent damage) {
    }

    default void onHeal(BossFightState.PlayerState ps, DamageEvent heal) {
    }

    default void onTurnBegin(BossFightState.PlayerState ps) {
    }

    default void onTurnEnd(BossFightState.PlayerState ps) {
    }

    default boolean isExpired() {
        return false;
    }

    EffectType getEffectType();
}
