package ru.tggc.botapp.fight.event.boss;

public record StunEvent(String username, double damage) implements BossActionEvent {
    @Override
    public Object[] getArgs() {
        return new Object[]{username, damage};
    }
}
