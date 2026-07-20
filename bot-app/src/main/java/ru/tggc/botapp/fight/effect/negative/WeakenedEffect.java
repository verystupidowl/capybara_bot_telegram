package ru.tggc.botapp.fight.effect.negative;

import ru.tggc.botapp.fight.BossFightState;
import ru.tggc.botapp.fight.DamageEvent;
import ru.tggc.botapp.fight.effect.AbstractExpiringEffect;
import ru.tggc.botapp.fight.effect.EffectType;

public class WeakenedEffect extends AbstractExpiringEffect {
    private final double damageReductionPercent;

    public WeakenedEffect(double damageReductionPercent, int turnsLeft) {
        super(turnsLeft);
        this.damageReductionPercent = damageReductionPercent;
    }

    @Override
    public void onDamageGiven(BossFightState.PlayerState ps, DamageEvent damage) {
        doEffect(() -> damage.setDamage(damageReductionPercent * damage.getDamage()));
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.NEGATIVE;
    }
}
