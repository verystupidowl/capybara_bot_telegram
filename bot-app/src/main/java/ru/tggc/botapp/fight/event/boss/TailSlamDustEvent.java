package ru.tggc.botapp.fight.event.boss;

public record TailSlamDustEvent(String username, double damage, int turnsLeft) implements BossActionEvent {
    @Override
    public Object[] getArgs() {
        return new Object[]{username, damage, turnsLeft};
    }
}
