package ru.tggc.capibaraBotTelegram.capybara.properties;

public class WeddingGiftDate extends AbstractCapybaraProperty {



    public int getTimeRemaining() {
        return timer;
    }

    public WeddingGiftDate(int giftTimer) {
        this.timer = giftTimer;
    }


    public void setTimer(int timer) {
        this.timer = timer;
    }

}
