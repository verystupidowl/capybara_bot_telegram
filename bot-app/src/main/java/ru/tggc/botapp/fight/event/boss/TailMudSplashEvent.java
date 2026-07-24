package ru.tggc.botapp.fight.event.boss;

public record TailMudSplashEvent() implements BossActionEvent {
    @Override
    public Object[] getArgs() {
        return new Object[]{};
    }
}
