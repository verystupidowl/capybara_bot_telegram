package ru.tggc.capybaratelegrambot.domain.model.enums;

import lombok.Getter;

@Getter
public enum ImprovementValue {
    BOOTS("Удобные ботиночки", 30, 10, 10),
    WATERMELON("Вкусный арбуз", -10, 10, 0),
    ANTI_LOSE("Антипроигрыш", 100, 10, 50),
    NONE("Ничего", 0, 10, 10);

    private final String label;
    private final int chance;
    private final int winHappiness;
    private final int loseHappiness;

    ImprovementValue(String label, Integer chance, int winHappiness, int loseHappiness) {
        this.label = label;
        this.chance = chance;
        this.winHappiness = winHappiness;
        this.loseHappiness = loseHappiness;
    }
}
