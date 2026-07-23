package ru.tggc.botapp.fight.event.boss;

public record TailSlamDustEvent(String username, double damage, int turnsLeft) implements BossActionEvent {
}
