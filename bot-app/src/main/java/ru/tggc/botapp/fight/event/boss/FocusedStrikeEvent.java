package ru.tggc.botapp.fight.event.boss;

public record FocusedStrikeEvent(String username, double damage) implements BossActionEvent {
}
