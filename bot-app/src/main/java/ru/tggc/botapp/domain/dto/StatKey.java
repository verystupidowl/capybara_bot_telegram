package ru.tggc.botapp.domain.dto;

import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.timedaction.Happiness;
import ru.tggc.botapp.domain.model.timedaction.Satiety;

import java.util.function.Function;

public record StatKey<T>(StatType statType, Function<Capybara, T> extractor) {
    public static StatKey<Happiness> HAPPINESS = new StatKey<>(StatType.HAPPINESS, Capybara::getHappiness);
    public static StatKey<Satiety> SATIETY = new StatKey<>(StatType.SATIETY, Capybara::getSatiety);

    public T extract(Capybara capybara) {
        return extractor.apply(capybara);
    }

    private enum StatType {
        HAPPINESS,
        SATIETY
    }
}
