package ru.tggc.capybaratelegrambot.domain.dto.fight.effect.positive;

import ru.tggc.capybaratelegrambot.domain.dto.fight.BossFightState;
import ru.tggc.capybaratelegrambot.domain.dto.fight.DamageEvent;
import ru.tggc.capybaratelegrambot.domain.dto.fight.effect.AbstractEffect;
import ru.tggc.capybaratelegrambot.domain.dto.fight.effect.EffectType;

public class VampirismEffect extends AbstractEffect {

    @Override
    public void onDamageGiven(BossFightState.PlayerState ps, BossFightState.BossState boss, DamageEvent damage) {
        double v = damage.getDamage() * ps.getPlayerStats().getVampirism();
        ps.getPlayerStats().setHp((int) (ps.getPlayerStats().getHp() + v));
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.POSITIVE;
    }
}
