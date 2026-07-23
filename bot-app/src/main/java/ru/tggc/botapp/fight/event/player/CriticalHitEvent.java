package ru.tggc.botapp.fight.event.player;

public record CriticalHitEvent(String username) implements PlayerActionEvent {
}
