package ru.tggc.capybaratelegrambot.domain.dto.fight.effect.negative;

import ru.tggc.capybaratelegrambot.domain.dto.fight.BossFightState;
import ru.tggc.capybaratelegrambot.domain.dto.fight.DamageEvent;
import ru.tggc.capybaratelegrambot.domain.dto.fight.effect.AbstractEffect;
import ru.tggc.capybaratelegrambot.domain.dto.fight.effect.EffectType;
import ru.tggc.capybaratelegrambot.utils.RandomUtils;

public class Blindness extends AbstractEffect {
    private final double chance;
    private int turnsLeft;

    public Blindness(double chance, int turnsLeft) {
        this.chance = chance;
        this.turnsLeft = turnsLeft;
    }

    @Override
    public void onDamageGiven(BossFightState.PlayerState ps, BossFightState.BossState boss, DamageEvent damage) {
        if (RandomUtils.chance(chance)) {
            damage.setDamage(0);
        }
        turnsLeft--;
    }

    @Override
    public boolean isExpired() {
        return turnsLeft <= 0;
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.NEGATIVE;
    }
}
