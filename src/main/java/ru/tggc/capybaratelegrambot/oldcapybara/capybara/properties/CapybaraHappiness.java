package ru.tggc.capybaratelegrambot.oldcapybara.capybara.properties;

import org.springframework.stereotype.Component;

@Component
@Deprecated(forRemoval = true)
public class CapybaraHappiness extends AbstractCapybaraProperty {


    public CapybaraHappiness() {
    }

    public CapybaraHappiness(int happinessTimer, int happiness) {
        this.timer = happinessTimer;
        this.level = happiness;
    }

    public Integer getTimeRemaining() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }


    public static CapybaraHappiness getNewTimer() {
        CapybaraHappiness capybaraHappiness = new CapybaraHappiness();
        capybaraHappiness.setLevel(0);
        capybaraHappiness.setTimer(0);
        return capybaraHappiness;
    }
}
