package ru.tggc.capybaratelegrambot.domain.dto.fight.effect;

import ru.tggc.capybaratelegrambot.domain.dto.fight.BossFightState;
import ru.tggc.capybaratelegrambot.domain.dto.fight.DamageEvent;

public interface Effect {

    default void onDamageTaken(BossFightState.PlayerState ps, BossFightState.BossState boss, DamageEvent damage) {
    }

    default void onDamageGiven(BossFightState.PlayerState ps, BossFightState.BossState boss, DamageEvent damage) {
    }

    default void onHeal(BossFightState.PlayerState ps, BossFightState.BossState boss, DamageEvent heal) {
    }

    default boolean isExpired() {
        return false;
    }

    EffectType getEffectType();
}
