package ru.tggc.botapp.fight.event.boss;

public record AoeStunEvent() implements BossActionEvent {
    @Override
    public Object[] getArgs() {
        return new Object[]{};
    }
}
