package ru.tggc.botapp.fight.event;

public sealed interface PlayerActionEvent extends FightEvent
        permits CriticalHitEvent, DamageDealtEvent, DefendEvent, HealEvent {
}
