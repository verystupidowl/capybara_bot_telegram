package ru.tggc.capybaratelegrambot.utils;

import lombok.Getter;

import java.util.function.UnaryOperator;

@Getter
public enum CasinoTargetType {
    BLACK("чёрное", betAmount -> betAmount * 2),
    RED("красное", betAmount -> betAmount * 2),
    ZERO("зеро", betAmount -> betAmount * 36);

    private final String label;
    private final UnaryOperator<Long> calculateWin;

    CasinoTargetType(String label, UnaryOperator<Long> calculateWin) {
        this.label = label;
        this.calculateWin = calculateWin;
    }
}
