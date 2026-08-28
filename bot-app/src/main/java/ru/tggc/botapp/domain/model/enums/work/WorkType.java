package ru.tggc.botapp.domain.model.enums.work;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.tggc.botapp.util.RandomUtils;

import java.util.function.UnaryOperator;

@Getter
@RequiredArgsConstructor
public enum WorkType {
    NONE("Безработный", new WorkIndex[0], _ -> 0),
    IT("Программист", ItIndex.values(), index -> index == 0 ? 0 : (index * 70) + RandomUtils.getRandomInt(index * 40)),
    CASHIER("Кассир", CashierIndex.values(), index -> 50 + (index * 30) + RandomUtils.getRandomInt(20)),
    CRIMINAL("Бандит", CriminalIndex.values(), index -> {
        int bustChance = RandomUtils.getRandomInt(100);
        if (bustChance < 35) {
            return -1;
        }
        int baseLoot = 100 + (index * 50);
        int randomBonus = RandomUtils.getRandomInt(100 + (index * 50));
        return baseLoot + randomBonus;
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
