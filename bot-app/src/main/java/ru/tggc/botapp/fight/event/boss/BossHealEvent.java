package ru.tggc.botapp.fight.event.boss;

public record BossHealEvent(double heal) implements BossActionEvent {
    @Override
    public Object[] getArgs() {
        return new Object[]{heal};
    }
}
