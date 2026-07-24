package ru.tggc.botapp.fight.event.boss;

public record BossCriticalHitEvent() implements BossActionEvent {
    @Override
    public Object[] getArgs() {
        return new Object[]{};
    }
}
