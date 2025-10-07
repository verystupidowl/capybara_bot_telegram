package ru.tggc.capybaratelegrambot.domain.dto.fight.effect.negative;

import ru.tggc.capybaratelegrambot.domain.dto.fight.BossFightState;
import ru.tggc.capybaratelegrambot.domain.dto.fight.DamageEvent;
import ru.tggc.capybaratelegrambot.domain.dto.fight.effect.AbstractEffect;
import ru.tggc.capybaratelegrambot.domain.dto.fight.effect.EffectType;

public class WeakenedEffect extends AbstractEffect {
    private final double damageReductionPercent;
    private int turnsLeft;

    public WeakenedEffect(double damageReductionPercent, int turnsLeft) {
        this.damageReductionPercent = damageReductionPercent;
        this.turnsLeft = turnsLeft;
    }

    @Override
    public void onDamageGiven(BossFightState.PlayerState ps, DamageEvent damage) {
        if (turnsLeft > 0) {
            damage.setDamage(damage.getDamage() * damageReductionPercent);
        }
        turnsLeft--;
    }

    @Override
    public boolean isExpired() {
        return turnsLeft <= 0;
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.NEGATIVE;
    }
}
