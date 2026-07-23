package ru.tggc.botapp.fight.event.player;

public record DamageDealtEvent(String username, double damage) implements PlayerActionEvent {
}
