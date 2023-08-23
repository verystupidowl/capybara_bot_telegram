package ru.tggc.capibaraBotTelegram.capybara.properties;

import org.springframework.stereotype.Component;

@Component
public class CapybaraJob extends AbstractCapybaraProperty {

    public CapybaraJob(int jobTimer, int onJob, Long nextJob) {
        this.timer = jobTimer;
        this.level = onJob;
        this.nextTime = nextJob;
    }

    public CapybaraJob() {

    }


    public Long getNextJob() {
        return nextTime;
    }

    public void setNextJob(Long nextJob) {
        this.nextTime = nextJob;
    }


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
}
