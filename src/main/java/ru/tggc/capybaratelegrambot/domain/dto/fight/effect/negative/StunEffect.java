package ru.tggc.capybaratelegrambot.domain.dto.fight.effect.negative;

import lombok.AllArgsConstructor;
import ru.tggc.capybaratelegrambot.domain.dto.fight.BossFightState;
import ru.tggc.capybaratelegrambot.domain.dto.fight.effect.AbstractEffect;
import ru.tggc.capybaratelegrambot.domain.dto.fight.effect.EffectType;

@AllArgsConstructor
public class StunEffect extends AbstractEffect {
    private int remainingTurns;

    @Override
    public void onTurnBegin(BossFightState.PlayerState ps) {
        ps.setCanAct(false);
    }

    @Override
    public void onTurnEnd(BossFightState.PlayerState ps) {
        remainingTurns--;
    }

    @Override
    public boolean isExpired() {
        return remainingTurns <= 0;
    }


    @Override
    public EffectType getEffectType() {
        return null;
    }
}
