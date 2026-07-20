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

    PLAYER_ATTACK("fight.player-attack"),
    PLAYER_CRITICAL_HIT("fight.player-critical-attack"),
    PLAYER_DEFEND("fight.player-defend"),
    PLAYER_HEAL("fight.player-heal"),
    ;

    private final String key;
}
