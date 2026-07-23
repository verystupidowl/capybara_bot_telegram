package ru.tggc.botapp.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum Type {
    COMMON("Обыкновенная капибара", 0, 0),
    FUNNY("Прикольная капибара", 10, 100),
    COOL("Крутая капибара", 20, 100),
    INCREDIBLE("Невероятная капибара", 30, 100),
    ROYAL("Королевская капибара", 40, 100),
    MAGICAL("Волшебная капибара", 50, 200),
    PLANETARY("Капибара планетарного масштаба", 60, 200),
    COSMIC("Космическая капибара", 70, 200),
    INTERGALACTIC("Межгалактическая капибара", 80, 250),
    UNIVERSAL("Капибара вселенского масштаба", 90, 250),
    SPACE_TIME("Капибара пространства и времени", 100, 250),
    CAPY_GOD("КапибараБог", 150, 1000),
    ;

    private final String label;
    private final int requiredLevel;
    private final Integer gift;

    public Optional<Type> next() {
        Type[] values = values();

        return ordinal() + 1 < values.length
                ? Optional.of(values[ordinal() + 1])
                : Optional.empty();
    }

    public int getMaxLevel() {
        return next()
                .map(Type::getRequiredLevel)
                .orElse(Integer.MAX_VALUE);
    }
}
