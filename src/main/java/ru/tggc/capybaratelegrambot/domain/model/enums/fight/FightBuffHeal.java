package ru.tggc.capybaratelegrambot.domain.model.enums.fight;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.tggc.capybaratelegrambot.domain.dto.BossFightState;

import java.util.function.Consumer;

@Getter
@AllArgsConstructor
public enum FightBuffHeal implements FightBuffEnum {
    NONE("Ничего", "Ничего не делает", 0, stats -> {
    }),
    SHIELD_LEAF("🍃🛡 Лист щита", "Улучшает действие щита на 25%", 100,
            player -> player.setBaseDefend(player.getBaseDefend() * 1.25));


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
        return BuffType.HEAL;
    }
}
