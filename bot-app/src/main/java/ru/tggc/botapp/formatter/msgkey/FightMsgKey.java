package ru.tggc.botapp.formatter.msgkey;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.tggc.telegrambotcore.formatter.MsgKey;

@AllArgsConstructor
@Getter
public enum FightMsgKey implements MsgKey {
    START_MESSAGE("fight.start-message"),
    PREPARING_USERS("fight.preparing-users"),
    CANT_ACT("fight.cant-act"),
    PLAYER_CHOSE("fight.player-chose"),
    FIGHT_INFO("fight.fight-info"),

    PLAYER_ATTACK("fight.action.player.attack"),
    PLAYER_CRITICAL_HIT("fight.action.player.critical-attack"),
    PLAYER_DEFEND("fight.action.player.defend"),
    PLAYER_HEAL("fight.action.player.heal"),

    BOSS_TAIL_ON_THE_WATER("fight.action.boss.tail-on-the-water"),
    BOSS_DAMAGE_DEALT("fight.action.boss.damage-dealt"),
    BOSS_BITE("fight.action.boss.bite"),
    BOSS_STUN("fight.action.boss.stun"),
    BOSS_AOE_DAMAGE("fight.action.boss.aoe-damage"),
    BOSS_AOE_STUN("fight.action.boss.aoe-stun"),
    BOSS_HEAL("fight.action.boss.heal"),
    BOSS_FOCUSED_STRIKE("fight.action.boss.focused-strike"),
    BOSS_POISON_BITE("fight.action.boss.poison-bite"),
    BOSS_TAIL_SLAM_DUST("fight.action.boss.tail-slam-dust"),
    BOSS_TAIL_MUD_SPLASH("fight.action.boss.tail-mud-splash"),
    BOSS_CRITICAL_HIT("fight.action.boss.critical-hit"),

    BOSS_ACTION_TEMPLATE("fight.action.boss.action-template"),
    ;

    private final String key;
}
