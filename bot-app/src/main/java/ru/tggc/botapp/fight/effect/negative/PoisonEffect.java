package ru.tggc.botapp.fight.effect.negative;

import ru.tggc.botapp.fight.BossFightState;
import ru.tggc.botapp.fight.DamageEvent;
import ru.tggc.botapp.fight.effect.AbstractEffect;
import ru.tggc.botapp.fight.effect.EffectType;

public class PoisonEffect extends AbstractEffect {
    private int stacks = 1;

    @Override
    public void onDamageTaken(BossFightState.PlayerState ps, DamageEvent damage) {
        damage.setDamage(damage.getDamage() + stacks * 2);
        this.stacks++;
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.NEGATIVE;
    }
}
