package ru.tggc.botapp.fight.event.player;

public record PlayerCriticalHitEvent(String username) implements PlayerActionEvent {
    @Override
    public Object[] getArgs() {
        return new Object[]{username};
    }
}
