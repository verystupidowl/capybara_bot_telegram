package ru.tggc.capybaratelegrambot.domain.dto.fight.effect.positive;

import lombok.AllArgsConstructor;
import ru.tggc.capybaratelegrambot.domain.dto.fight.BossFightState;
import ru.tggc.capybaratelegrambot.domain.dto.fight.DamageEvent;
import ru.tggc.capybaratelegrambot.domain.dto.fight.effect.AbstractEffect;
import ru.tggc.capybaratelegrambot.domain.dto.fight.effect.EffectType;

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
