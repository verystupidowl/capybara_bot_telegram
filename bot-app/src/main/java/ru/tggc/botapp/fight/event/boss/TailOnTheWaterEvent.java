package ru.tggc.botapp.fight.event.boss;

public record TailOnTheWaterEvent() implements BossActionEvent {
    @Override
    public Object[] getArgs() {
        return new Object[]{};
    }
}
