package ru.tggc.botapp.fight.event;

public record CriticalHitEvent(String username) implements PlayerActionEvent {
}
