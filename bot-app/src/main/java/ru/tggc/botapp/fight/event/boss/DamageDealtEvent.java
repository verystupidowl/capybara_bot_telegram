package ru.tggc.botapp.fight.event.boss;

public record DamageDealtEvent(String username, double damage) implements BossActionEvent {
}
