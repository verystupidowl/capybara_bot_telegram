package ru.tggc.capybaratelegrambot.domain.fight.effect.positive;

import lombok.AllArgsConstructor;
import ru.tggc.capybaratelegrambot.domain.fight.BossFightState;
import ru.tggc.capybaratelegrambot.domain.fight.DamageEvent;
import ru.tggc.capybaratelegrambot.domain.fight.effect.AbstractEffect;
import ru.tggc.capybaratelegrambot.domain.fight.effect.EffectType;

@AllArgsConstructor
public class VampirismEffect extends AbstractEffect {
    private final double value;

    @Override
    public void onDamageGiven(BossFightState.PlayerState ps, DamageEvent damage) {
        double v = damage.getDamage() * value;
        ps.getPlayerStats().setHp((int) (ps.getPlayerStats().getHp() + v));
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.POSITIVE;
    }
}
