package ru.tggc.capybaratelegrambot.domain.model.enums.fight;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.tggc.capybaratelegrambot.domain.dto.BossFightState;

import java.util.function.Consumer;

@AllArgsConstructor
@Getter
public enum FightBuffShield implements FightBuffEnum {
    NONE("Ничего", "Совсем ничего", 0, stats -> {
    }),
    HEALING_HERB("🌿 Зелье травницы", "Увеличивает восстановление хп на 25%", 100,
            stats -> stats.setBaseHeal(stats.getBaseHeal() * 1.25)),
    ;

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
