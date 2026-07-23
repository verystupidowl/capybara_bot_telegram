package ru.tggc.botapp.fight.event.boss;

public record BiteEvent(String username, double damage) implements BossActionEvent {
}
