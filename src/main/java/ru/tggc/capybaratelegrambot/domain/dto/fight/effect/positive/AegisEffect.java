package ru.tggc.capybaratelegrambot.domain.dto.fight.effect.positive;

import ru.tggc.capybaratelegrambot.domain.dto.fight.BossFightState;
import ru.tggc.capybaratelegrambot.domain.dto.fight.effect.AbstractEffect;
import ru.tggc.capybaratelegrambot.domain.dto.fight.effect.EffectType;

public class AegisEffect extends AbstractEffect {
    private int count = 1;

    @Override
    public void onTurnEnd(BossFightState.PlayerState ps) {
        if (!ps.isAlive()) {
            ps.getPlayerStats().getEffects()
                    .removeIf(e -> e.getEffectType() == EffectType.NEGATIVE);
            ps.setAlive(true);
            ps.getPlayerStats().setHp(50);
            count--;
        }
    }

    @Override
    public boolean isExpired() {
        return count > 0;
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.POSITIVE;
    }
}
