package ru.tggc.capybaratelegrambot.domain.fight.effect.negative;

import ru.tggc.capybaratelegrambot.domain.fight.BossFightState;
import ru.tggc.capybaratelegrambot.domain.fight.DamageEvent;
import ru.tggc.capybaratelegrambot.domain.fight.effect.AbstractExpiringEffect;
import ru.tggc.capybaratelegrambot.domain.fight.effect.EffectType;
import ru.tggc.capybaratelegrambot.utils.RandomUtils;

public class BlindnessEffect extends AbstractExpiringEffect {
    private final double chance;

    public BlindnessEffect(double chance, int turnsLeft) {
        super(turnsLeft);
        this.chance = chance;
    }

    @Override
    public void onDamageGiven(BossFightState.PlayerState ps, DamageEvent damage) {
        doEffect(() -> {
            if (RandomUtils.chance(chance)) {
                damage.setDamage(0);
            }
        });
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.NEGATIVE;
    }
}
