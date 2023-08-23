package ru.tggc.capibaraBotTelegram.capybara;

import java.util.ArrayList;
import java.util.List;

public class Happiness {
    private final List<String> happinessThings = new ArrayList<>();


    public void setHappinessThings() {
        happinessThings.add("Ты погладил капибару!\nСчастье увеличилось на 5");
        happinessThings.add("Ты покормил капибару!\nСчастье увеличилось на 20, а сытость на 5!");
        happinessThings.add("Ты погулял с капибарой!\nСчастье увеличилось на 15");
        happinessThings.add("Ты поиграл с капибарой!\nСчастье увеличилось на 10");
        happinessThings.add("Ты поругал капибару\nСчастье уменьшилось на 10");
    }

    public List<String> getHappinessThings() {
        setHappinessThings();
        return happinessThings;
    }
}
