package ru.tggc.botapp.util;

import lombok.Getter;

import java.util.function.UnaryOperator;

@Getter
public enum CasinoTargetType {
    BLACK("чёрное", betAmount -> betAmount * 2),
    RED("красное", betAmount -> betAmount * 2),
    ZERO("зеро", betAmount -> betAmount * 36);

    private final String label;
    private final UnaryOperator<Long> winCalculator;

    CasinoTargetType(String label, UnaryOperator<Long> winCalculator) {
        this.label = label;
        this.winCalculator = winCalculator;
    }

    public Long calculate(Long amount) {
        return winCalculator.apply(amount);
    }
}
