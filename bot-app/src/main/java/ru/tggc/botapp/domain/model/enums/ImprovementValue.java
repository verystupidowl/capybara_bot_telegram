package ru.tggc.botapp.domain.model.enums;

import lombok.Getter;

@Getter
public enum ImprovementValue {
    BOOTS("Удобные ботиночки", 30, 10, 10, 50),
    WATERMELON("Вкусный арбуз", -10, 10, 0, 100),
    ANTI_LOSE("Антипроигрыш", 100, 10, 50, 150),
    NONE("Ничего", 0, 10, 10, 0);

    private final String label;
    private final int chance;
    private final int winHappiness;
    private final int loseHappiness;
    private final int cost;

    ImprovementValue(String label, Integer chance, int winHappiness, int loseHappiness, int cost) {
        this.label = label;
        this.chance = chance;
        this.winHappiness = winHappiness;
        this.loseHappiness = loseHappiness;
        this.cost = cost;
    }
}
