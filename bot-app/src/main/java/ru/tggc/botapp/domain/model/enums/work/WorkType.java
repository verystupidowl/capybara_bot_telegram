package ru.tggc.botapp.domain.model.enums.work;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.tggc.botapp.util.RandomUtils;

import java.util.function.UnaryOperator;

@Getter
@RequiredArgsConstructor
public enum WorkType {
    NONE("Безработный", new WorkIndex[0], null),
    IT("Программист", ItIndex.values(), index -> index != 0 ? RandomUtils.getRandomInt(index * 100) + 100 : 0),
    CASHIER("Кассир", CashierIndex.values(), index -> RandomUtils.getRandomInt(index + 10) * 200 + 1),
    CRIMINAL("Бандит", CriminalIndex.values(), index -> {
        int randomSalary = RandomUtils.getRandomInt(index + 10) * 10 + 30;
        if (randomSalary < 100) {
            return -1;
        }
        return randomSalary;
    });

    private final String label;
    private final WorkIndex[] levels;
    private final UnaryOperator<Integer> calculateSalary;

    public Integer calculateSalary(Integer index) {
        return calculateSalary.apply(index);
    }

    public WorkIndex getLevelByIndex(int index) {
        if (this == NONE || levels.length == 0) {
            return null;
        }
        if (index >= levels.length) {
            return levels[levels.length - 1];
        }
        return levels[index];
    }

    public boolean hasNewLevel(int currentIndex) {
        return currentIndex < levels.length - 1;
    }
}
