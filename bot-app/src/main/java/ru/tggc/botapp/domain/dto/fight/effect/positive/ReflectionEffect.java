package ru.tggc.botapp.domain.dto.fight.effect.positive;

import ru.tggc.botapp.domain.dto.fight.BossFightState;
import ru.tggc.botapp.domain.dto.fight.DamageEvent;
import ru.tggc.botapp.domain.dto.fight.effect.AbstractEffect;
import ru.tggc.botapp.domain.dto.fight.effect.EffectType;

import java.math.BigDecimal;

public class ReflectionEffect extends AbstractEffect {
    private final BigDecimal damageReflection;

    public ReflectionEffect(BigDecimal damageReflection) {
        this.damageReflection = damageReflection;
    }

    @Override
    public void onDamageTaken(BossFightState.PlayerState ps, DamageEvent damage) {
        BossFightState.BossState boss = ps.getBoss();
        int reflectedDamage = (damageReflection.multiply(new BigDecimal(damage.getDamage()))).toBigInteger().intValue();
        boss.setBossHp(boss.getBossHp() - reflectedDamage);
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.POSITIVE;
    }
}
