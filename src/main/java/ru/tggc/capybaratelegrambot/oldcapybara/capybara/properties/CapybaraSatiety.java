package ru.tggc.capybaratelegrambot.oldcapybara.capybara.properties;

import org.springframework.stereotype.Component;


@Component
@Deprecated(forRemoval = true)
public class CapybaraSatiety extends AbstractCapybaraProperty {

    public CapybaraSatiety() {

    }

    public CapybaraSatiety(int satietyTimer, int satiety) {
        this.timer = satietyTimer;
        this.level = satiety;
    }

    public static CapybaraSatiety getNewTimer() {
        CapybaraSatiety satietyTimer = new CapybaraSatiety();
        satietyTimer.setLevel(0);
        satietyTimer.setTimer(0);
        return satietyTimer;
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
}
