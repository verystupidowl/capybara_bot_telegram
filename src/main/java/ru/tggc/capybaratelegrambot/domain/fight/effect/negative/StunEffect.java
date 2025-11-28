package ru.tggc.capybaratelegrambot.domain.fight.effect.negative;

import lombok.AllArgsConstructor;
import ru.tggc.capybaratelegrambot.domain.fight.BossFightState;
import ru.tggc.capybaratelegrambot.domain.fight.effect.AbstractEffect;
import ru.tggc.capybaratelegrambot.domain.fight.effect.EffectType;

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
        //todo придумать что-то типа onExpiration, чтобы стан уходил после удаления из списка эффектов
    }

    @Override
    public boolean isExpired() {
        return remainingTurns <= 0;
    }


    @Override
    public EffectType getEffectType() {
        return EffectType.NEGATIVE;
    }
}
