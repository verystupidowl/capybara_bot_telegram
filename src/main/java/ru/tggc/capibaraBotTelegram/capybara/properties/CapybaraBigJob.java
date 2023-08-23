package ru.tggc.capibaraBotTelegram.capybara.properties;

public class CapybaraBigJob extends AbstractCapybaraProperty {

    public CapybaraBigJob(int jobTimer, int onJob, Long nextJob) {
        this.timer = jobTimer;
        this.level = onJob;
        this.nextTime = nextJob;
    }

    public CapybaraBigJob() {

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

    @Override
    public String toString() {
        return "CapybaraBigJob{" +
                "timer=" + timer +
                ", level=" + level +
                ", nextTime=" + nextTime +
                '}';
    }
}
