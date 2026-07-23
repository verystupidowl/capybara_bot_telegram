package ru.tggc.botapp.fight.event.boss;

import ru.tggc.botapp.fight.event.FightEvent;

public sealed interface BossActionEvent
        extends FightEvent
        permits AoeDamageEvent,
        AoeStunEvent,
        BiteEvent,
        BossCriticalHitEvent,
        DamageDealtEvent,
        FocusedStrikeEvent,
        HealEvent,
        PoisonBiteEvent,
        StunEvent,
        TailMudSplashEvent,
        TailOnTheWaterEvent,
        TailSlamDustEvent {
}
