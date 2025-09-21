package ru.tggc.capybaratelegrambot.domain.model.enums;

import lombok.Getter;
import ru.tggc.capybaratelegrambot.utils.RandomUtils;

import java.util.function.UnaryOperator;

@Getter
public enum JobType {
    NONE("Безработный", null),
    PROGRAMMING("Программист", index -> index != 0 ? RandomUtils.getRandomInt(index * 100) + 100 : 0),
    CRIMINAL("Бандит", index -> RandomUtils.getRandomInt(index + 1) * 200 + 1),
    CASHIER("Кассир", index -> {
        int randomSalary = RandomUtils.getRandomInt(index + 1) * 10 + 30;
        if (randomSalary < 100) {
            return -1;
        }
        return randomSalary;
    });

    private final String label;
    private final UnaryOperator<Integer> calculateSalary;

    JobType(String label, UnaryOperator<Integer> calculateSalary) {
        this.label = label;
        this.calculateSalary = calculateSalary;
    }
}
