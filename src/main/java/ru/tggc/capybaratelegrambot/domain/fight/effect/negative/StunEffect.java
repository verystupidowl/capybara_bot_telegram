package ru.tggc.capybaratelegrambot.domain.fight.effect.negative;

import ru.tggc.capybaratelegrambot.domain.fight.BossFightState;
import ru.tggc.capybaratelegrambot.domain.fight.effect.AbstractExpiringEffect;
import ru.tggc.capybaratelegrambot.domain.fight.effect.EffectType;

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
        doEffect(() -> {
        });
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
