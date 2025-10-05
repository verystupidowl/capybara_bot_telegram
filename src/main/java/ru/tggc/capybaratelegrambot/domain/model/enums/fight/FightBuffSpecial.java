package ru.tggc.capybaratelegrambot.domain.model.enums.fight;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.tggc.capybaratelegrambot.domain.dto.fight.BossFightState;
import ru.tggc.capybaratelegrambot.domain.dto.fight.effect.positive.AegisEffect;

import java.util.function.Consumer;

@AllArgsConstructor
@Getter
public enum FightBuffSpecial implements FightBuffEnum {
    NONE("none", "none", 0, player -> {
    }),
    AEGIS("Аегис", "В разработке. Пока ничего не дает", 500, stats ->
            stats.getEffects().add(new AegisEffect()));

    private final String title;
    private final String description;
    private final int cost;
    private final Consumer<BossFightState.PlayerStats> effect;

    @Override
    public void apply(BossFightState.PlayerStats player) {
        effect.accept(player);
    }

    @Override
    public BuffType getBuffType() {
        return BuffType.SPECIAL;
    }
}
