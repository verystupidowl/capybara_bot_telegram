package ru.tggc.botapp.fight.effect.positive;

import ru.tggc.botapp.fight.BossFightState;
import ru.tggc.botapp.fight.DamageEvent;
import ru.tggc.botapp.fight.effect.AbstractEffect;
import ru.tggc.botapp.fight.effect.EffectType;

public class AntiDebuffEffect extends AbstractEffect {

    @Override
    public void onHeal(BossFightState.PlayerState ps, DamageEvent damage) {
        ps.getPlayerStats().getEffects().removeIf(e -> e.getEffectType() == EffectType.NEGATIVE);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.POSITIVE;
    }
}
