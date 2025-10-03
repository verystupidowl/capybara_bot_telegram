package ru.tggc.capybaratelegrambot.domain.model.enums.fight;

import ru.tggc.capybaratelegrambot.domain.dto.BossFightState;

public interface FightBuffEnum {

    void apply(BossFightState.PlayerStats stats);

    String getTitle();

    String getDescription();

    String name();

    BuffType getBuffType();

    int getCost();
}
