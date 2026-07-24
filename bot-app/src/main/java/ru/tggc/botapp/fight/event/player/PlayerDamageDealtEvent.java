package ru.tggc.botapp.fight.event.player;

public record PlayerDamageDealtEvent(String username, double damage) implements PlayerActionEvent {
    @Override
    public Object[] getArgs() {
        return new Object[]{username, damage};
    }
}
