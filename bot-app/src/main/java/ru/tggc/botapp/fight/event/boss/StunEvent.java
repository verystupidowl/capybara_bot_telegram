package ru.tggc.botapp.fight.event.boss;

public record StunEvent(String username, double damage) implements BossActionEvent {
}
