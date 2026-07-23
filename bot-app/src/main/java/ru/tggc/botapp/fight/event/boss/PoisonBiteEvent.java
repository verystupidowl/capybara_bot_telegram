package ru.tggc.botapp.fight.event.boss;

public record PoisonBiteEvent(String username, double damage) implements BossActionEvent {
}
