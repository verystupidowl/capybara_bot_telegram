package ru.tggc.botapp.fight.event;

public record HealEvent(String username, double heal) implements PlayerActionEvent {
}
