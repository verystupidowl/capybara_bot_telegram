package ru.tggc.botapp.fight.event.player;

public record PlayerDefendEvent(String username) implements PlayerActionEvent {
    @Override
    public Object[] getArgs() {
        return new Object[]{username};
    }
}
