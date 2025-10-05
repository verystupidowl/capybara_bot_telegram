package ru.tggc.capybaratelegrambot.domain.dto.fight.effect.positive;

import ru.tggc.capybaratelegrambot.domain.dto.fight.BossFightState;
import ru.tggc.capybaratelegrambot.domain.dto.fight.DamageEvent;
import ru.tggc.capybaratelegrambot.domain.dto.fight.effect.AbstractEffect;
import ru.tggc.capybaratelegrambot.domain.dto.fight.effect.EffectType;

public class ReflectionEffect extends AbstractEffect {

    @Override
    public void onDamageTaken(BossFightState.PlayerState ps, BossFightState.BossState boss, DamageEvent damage) {
        int reflectedDamage = (int) (damage.getDamage() * ps.getPlayerStats().getDamageReflection());
        boss.setBossHp(boss.getBossHp() - reflectedDamage);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.POSITIVE;
    }
}
