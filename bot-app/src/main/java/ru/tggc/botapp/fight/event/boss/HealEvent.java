package ru.tggc.botapp.fight.event.boss;

public record HealEvent(double heal) implements BossActionEvent {
}
