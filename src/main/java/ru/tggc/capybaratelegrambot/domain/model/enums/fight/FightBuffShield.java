package ru.tggc.capybaratelegrambot.domain.model.enums.fight;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.tggc.capybaratelegrambot.domain.fight.BossFightState;
import ru.tggc.capybaratelegrambot.domain.fight.effect.positive.ReflectionEffect;

import java.util.function.Consumer;

@AllArgsConstructor
@Getter
public enum FightBuffShield implements FightBuffEnum {
    NONE("Ничего", "Совсем ничего", 0, stats -> {
    }),
    SHIELD_LEAF("🍃🛡 Лист щита", "Улучшает действие щита на 25%", 100,
            player -> player.setBaseDefend(player.getBaseDefend() * 1.25)),
    BLADE_MALE("\uD83D\uDC21\uD83E\uDD94Колючая броня", "Отражает 80% урона, нанесенного капибаре обратно в обидчика, но снижает действие щита на 10%", 150,
            stats -> {
                stats.setBaseDefend(stats.getBaseDefend() * 0.9);
                stats.setDamageReflection(0.8);
                stats.getEffects().add(new ReflectionEffect());
            });

    private final String title;
    private final String description;
    private final int cost;
    private final Consumer<BossFightState.PlayerStats> effect;

    @Override
    public void apply(BossFightState.PlayerStats stats) {
        effect.accept(stats);
    }

    @Override
    public BuffType getBuffType() {
        return BuffType.DEFEND;
    }
}
