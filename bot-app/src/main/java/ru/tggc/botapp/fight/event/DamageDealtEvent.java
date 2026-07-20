package ru.tggc.botapp.fight.event;

public record DamageDealtEvent(String username, double damage) implements PlayerActionEvent {
}
