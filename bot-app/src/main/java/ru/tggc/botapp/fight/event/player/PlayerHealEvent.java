package ru.tggc.botapp.fight.event.player;

public record PlayerHealEvent(String username, double heal) implements PlayerActionEvent {
    @Override
    public Object[] getArgs() {
        return new Object[]{username, heal};
    }
}
