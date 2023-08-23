package ru.tggc.capibaraBotTelegram.capybara.properties;

public class CapybaraPreparation extends AbstractCapybaraProperty {


    public CapybaraPreparation(int improvement, int onJob, Long prepared) {
        this.timer = improvement;
        this.level = onJob;
        this.nextTime = prepared;
    }

    public CapybaraPreparation() {

    }

    public int getImprovement() {
        return timer;
    }


    public void setImprovement(int timer) {
        this.timer = timer;
    }


    public int getOnJob() {
        return level;
    }


    public void setOnJob(int level) {
        this.level = level;
    }

    public void setPrepared(Long nextTime) {
        this.nextTime = nextTime;
    }

    public Long getPrepared() {
        return nextTime;
    }

    public Integer getImprove ()  {
        return switch (getImprovement()) {
            case 1 -> 1337;
            case 2 -> 3337;
            case 3 -> 300;
            default -> -1;
        };
    }
}
