package ru.tggc.botapp.domain.model.enums.fight;

import ru.tggc.botapp.domain.dto.fight.BossFightState;

public interface FightBuffEnum {

    void apply(BossFightState.PlayerStats stats);

    String getTitle();

    String getDescription();

    String name();

    BuffType getBuffType();

    int getCost();
}
