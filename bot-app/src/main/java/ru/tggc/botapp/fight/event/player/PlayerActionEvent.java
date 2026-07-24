package ru.tggc.botapp.fight.event.player;

import ru.tggc.botapp.fight.event.FightEvent;

public sealed interface PlayerActionEvent extends FightEvent
        permits PlayerCriticalHitEvent, PlayerDamageDealtEvent, PlayerDefendEvent, PlayerHealEvent {
}
