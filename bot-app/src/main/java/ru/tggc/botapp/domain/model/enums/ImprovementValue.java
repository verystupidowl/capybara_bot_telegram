package ru.tggc.botapp.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
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
}
