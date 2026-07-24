package ru.tggc.botapp.fight.event.boss;

public record AoeDamageEvent() implements BossActionEvent {

    @Override
    public Object[] getArgs() {
        return new Object[]{};
    }
}
