package ru.tggc.capybaratelegrambot.domain.fight.effect.positive;

import ru.tggc.capybaratelegrambot.domain.fight.BossFightState;
import ru.tggc.capybaratelegrambot.domain.fight.DamageEvent;
import ru.tggc.capybaratelegrambot.domain.fight.effect.AbstractEffect;
import ru.tggc.capybaratelegrambot.domain.fight.effect.EffectType;

public class ReflectionEffect extends AbstractEffect {

    @Override
    public void onDamageTaken(BossFightState.PlayerState ps, DamageEvent damage) {
        BossFightState.BossState boss = ps.getBoss();
        int reflectedDamage = (int) (damage.getDamage() * ps.getPlayerStats().getDamageReflection());
        boss.setBossHp(boss.getBossHp() - reflectedDamage);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.POSITIVE;
    }
}
