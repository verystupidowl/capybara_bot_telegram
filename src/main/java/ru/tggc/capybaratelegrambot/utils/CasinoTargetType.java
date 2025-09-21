package ru.tggc.capybaratelegrambot.utils;

import lombok.Getter;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;

import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

@Getter
public enum CasinoTargetType {
    BLACK(List.of("черное", "чёрное"), betAmount -> betAmount * 2),
    RED(List.of("красное"), betAmount -> betAmount * 2),
    ZERO(List.of("зеленое", "зелёное", "зеро"), betAmount -> betAmount * 36);

    private final List<String> labels;
    private final UnaryOperator<Long> calculateWin;

    CasinoTargetType(List<String> labels, UnaryOperator<Long> calculateWin) {
        this.labels = labels;
        this.calculateWin = calculateWin;
    }

    public static CasinoTargetType getByLabel(String label) {
        return Arrays.stream(CasinoTargetType.values())
                .filter(it -> it.labels.stream().anyMatch(i -> i.contains(label)))
                .findFirst()
                .orElseThrow(() -> new CapybaraException("Неверное обозначение"));
    }
}
