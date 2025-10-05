package ru.tggc.capybaratelegrambot.domain.dto.fight.effect.negative;

import ru.tggc.capybaratelegrambot.domain.dto.fight.BossFightState;
import ru.tggc.capybaratelegrambot.domain.dto.fight.DamageEvent;
import ru.tggc.capybaratelegrambot.domain.dto.fight.effect.AbstractEffect;
import ru.tggc.capybaratelegrambot.domain.dto.fight.effect.EffectType;

public class PoisonEffect extends AbstractEffect {
    private int stacks = 1;

    @Override
    public void onDamageTaken(BossFightState.PlayerState ps, BossFightState.BossState boss, DamageEvent damage) {
        damage.setDamage(damage.getDamage() + stacks * 2);
        this.stacks++;
    }

    @Override
    public EffectType getEffectType() {
        return null;
    }
}
