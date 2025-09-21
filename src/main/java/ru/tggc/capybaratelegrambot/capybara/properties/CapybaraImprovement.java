package ru.tggc.capybaratelegrambot.capybara.properties;

import org.springframework.stereotype.Component;

@Component
@Deprecated(forRemoval = true)
public class CapybaraImprovement extends AbstractCapybaraProperty {


    public int getTimeRemaining() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getImprove() {
        switch (getLevel()) {
            case 1 -> {
                return 30;
            }
            case 2 -> {
                return -10;
            }
            case 3 -> {
                return 100;
            }
            default -> {
                return 0;
            }
        }
    }

    @Override
    public String toString() {
        return (getLevel() == 0) ? "Нет" : ((getLevel() == 1) ? "\uD83E\uDD7EУдобные ботиночки" :
                (getLevel() == 2) ? "\uD83C\uDF49Вкусный арбуз" : "\uD83D\uDC8AАнтипроигрыш");
    }
}
