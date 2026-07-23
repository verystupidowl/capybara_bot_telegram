package ru.tggc.botapp.fight.event.player;

public record HealEvent(String username, double heal) implements PlayerActionEvent {
}
