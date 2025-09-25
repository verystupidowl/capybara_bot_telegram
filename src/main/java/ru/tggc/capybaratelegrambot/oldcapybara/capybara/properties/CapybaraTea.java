package ru.tggc.capybaratelegrambot.oldcapybara.capybara.properties;

import org.springframework.stereotype.Component;


@Component
@Deprecated(forRemoval = true)
public class CapybaraTea extends AbstractCapybaraProperty {


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

    public CapybaraTea() {
    }

    public CapybaraTea(int teaTimer, int tea) {
        this.timer = teaTimer;
        this.level = tea;
    }

    public static CapybaraTea getNewTimer() {
        CapybaraTea capybaraTea = new CapybaraTea();
        capybaraTea.setLevel(0);
        capybaraTea.setTimer(0);
        return capybaraTea;
    }
}
