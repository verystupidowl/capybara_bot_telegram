package ru.tggc.capybaratelegrambot.domain.fight.effect.positive;

import ru.tggc.capybaratelegrambot.domain.fight.BossFightState;
import ru.tggc.capybaratelegrambot.domain.fight.effect.AbstractExpiringEffect;
import ru.tggc.capybaratelegrambot.domain.fight.effect.EffectType;

public class AegisEffect extends AbstractExpiringEffect {

    public AegisEffect() {
        super(1);
    }

    @Override
    public void onTurnEnd(BossFightState.PlayerState ps) {
        doEffect(() -> {
            if (!ps.isAlive()) {
                ps.getPlayerStats().getEffects().removeIf(e -> e.getEffectType() == EffectType.NEGATIVE);
                ps.setAlive(true);
                ps.getPlayerStats().setHp(50);
            }
        });
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.POSITIVE;
    }
}
