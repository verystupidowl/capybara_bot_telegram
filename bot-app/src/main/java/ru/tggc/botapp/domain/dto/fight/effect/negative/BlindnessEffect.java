package ru.tggc.botapp.domain.dto.fight.effect.negative;

import ru.tggc.botapp.domain.dto.fight.BossFightState;
import ru.tggc.botapp.domain.dto.fight.DamageEvent;
import ru.tggc.botapp.domain.dto.fight.effect.AbstractExpiringEffect;
import ru.tggc.botapp.domain.dto.fight.effect.EffectType;
import ru.tggc.botapp.util.RandomUtils;

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
