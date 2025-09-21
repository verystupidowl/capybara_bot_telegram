package ru.tggc.capybaratelegrambot.capybara.properties;

import org.springframework.stereotype.Component;


@Component
@Deprecated(forRemoval = true)
public class CapybaraRace extends AbstractCapybaraProperty {

    private int startedRace;


    public String getWantsRace() {
        return nextTime.toString();
    }

    public void setWantsRace(String wantsRace) {
        this.nextTime = Long.parseLong(wantsRace);
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

    public CapybaraRace() {
    }

    public CapybaraRace(int raceTimer, int race, String wantsRace, int startedRace) {
        this.timer = raceTimer;
        this.level = race;
        this.nextTime = Long.parseLong(wantsRace);
        this.startedRace = startedRace;
    }

    public static CapybaraRace getNewTimer() {
        CapybaraRace capybaraRace = new CapybaraRace();
        capybaraRace.setLevel(0);
        capybaraRace.setTimer(0);
        capybaraRace.setWantsRace("0");
        return capybaraRace;
    }

    public int getStartedRace() {
        return startedRace;
    }

    public void setStartedRace(int startedRace) {
        this.startedRace = startedRace;
    }

    @Override
    public String toString() {
        return "CapybaraRace{" +
                "startedRace=" + startedRace +
                ", timer=" + timer +
                ", level=" + level +
                ", nextTime=" + nextTime +
                '}';
    }
}
