package ru.tggc.botapp.formatter.msgkey;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.tggc.botapp.fight.event.FightEvent;
import ru.tggc.botapp.fight.event.boss.AoeDamageEvent;
import ru.tggc.botapp.fight.event.boss.AoeStunEvent;
import ru.tggc.botapp.fight.event.boss.BiteEvent;
import ru.tggc.botapp.fight.event.boss.BossCriticalHitEvent;
import ru.tggc.botapp.fight.event.boss.BossDamageDealtEvent;
import ru.tggc.botapp.fight.event.boss.BossHealEvent;
import ru.tggc.botapp.fight.event.boss.FocusedStrikeEvent;
import ru.tggc.botapp.fight.event.boss.PoisonBiteEvent;
import ru.tggc.botapp.fight.event.boss.StunEvent;
import ru.tggc.botapp.fight.event.boss.TailMudSplashEvent;
import ru.tggc.botapp.fight.event.boss.TailOnTheWaterEvent;
import ru.tggc.botapp.fight.event.boss.TailSlamDustEvent;
import ru.tggc.botapp.fight.event.player.PlayerCriticalHitEvent;
import ru.tggc.botapp.fight.event.player.PlayerDamageDealtEvent;
import ru.tggc.botapp.fight.event.player.PlayerDefendEvent;
import ru.tggc.botapp.fight.event.player.PlayerHealEvent;
import ru.tggc.telegrambotcore.formatter.MsgKey;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum FightEventMsgKey implements MsgKey {
    PLAYER_ATTACK("fight.action.player.attack", PlayerDamageDealtEvent.class),
    PLAYER_CRITICAL_HIT("fight.action.player.critical-attack", PlayerCriticalHitEvent.class),
    PLAYER_DEFEND("fight.action.player.defend", PlayerDefendEvent.class),
    PLAYER_HEAL("fight.action.player.heal", PlayerHealEvent.class),

    BOSS_TAIL_ON_THE_WATER("fight.action.boss.tail-on-the-water", TailOnTheWaterEvent.class),
    BOSS_DAMAGE_DEALT("fight.action.boss.damage-dealt", BossDamageDealtEvent.class),
    BOSS_BITE("fight.action.boss.bite", BiteEvent.class),
    BOSS_STUN("fight.action.boss.stun", StunEvent.class),
    BOSS_AOE_DAMAGE("fight.action.boss.aoe-damage", AoeDamageEvent.class),
    BOSS_AOE_STUN("fight.action.boss.aoe-stun", AoeStunEvent.class),
    BOSS_HEAL("fight.action.boss.heal", BossHealEvent.class),
    BOSS_FOCUSED_STRIKE("fight.action.boss.focused-strike", FocusedStrikeEvent.class),
    BOSS_POISON_BITE("fight.action.boss.poison-bite", PoisonBiteEvent.class),
    BOSS_TAIL_SLAM_DUST("fight.action.boss.tail-slam-dust", TailSlamDustEvent.class),
    BOSS_TAIL_MUD_SPLASH("fight.action.boss.tail-mud-splash", TailMudSplashEvent.class),
    BOSS_CRITICAL_HIT("fight.action.boss.critical-hit", BossCriticalHitEvent.class),
    ;

    private final String key;
    private final Class<? extends FightEvent> eventClass;

    public static MsgKey getMsgKeyByEvent(Class<? extends FightEvent> eventClass) {
        return Arrays.stream(values())
                .filter(e -> e.getEventClass().equals(eventClass))
                .findFirst()
                .orElse(null);
    }
}
