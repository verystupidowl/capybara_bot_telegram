package ru.tggc.botapp.domain.dto.fight.effect.positive;

import ru.tggc.botapp.domain.dto.fight.BossFightState;
import ru.tggc.botapp.domain.dto.fight.DamageEvent;
import ru.tggc.botapp.domain.dto.fight.effect.AbstractEffect;
import ru.tggc.botapp.domain.dto.fight.effect.EffectType;

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
