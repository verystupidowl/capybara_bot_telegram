package ru.tggc.botapp.domain.dto.fight.effect.negative;

import ru.tggc.botapp.domain.dto.fight.BossFightState;
import ru.tggc.botapp.domain.dto.fight.effect.AbstractExpiringEffect;
import ru.tggc.botapp.domain.dto.fight.effect.EffectType;

public class StunEffect extends AbstractExpiringEffect {

    public StunEffect() {
        super(1);
    }

    public StunEffect(int turnsLeft) {
        super(turnsLeft);
    }

    @Override
    public void onTurnBegin(BossFightState.PlayerState ps) {
        ps.setCanAct(false);
    }

    @Override
    public void onTurnEnd(BossFightState.PlayerState ps) {
        decreaseTurnsLeft();
    }

    @Override
    public void onExpired(BossFightState.PlayerState ps) {
        ps.setCanAct(true);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.NEGATIVE;
    }
}
