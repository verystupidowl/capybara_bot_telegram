package ru.tggc.botapp.formatter.msgkey;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FightMsgKey implements MsgKey {
    FIGHT_START_MESSAGE("fight.start-message"),
    FIGHT_PREPARING_USERS("fight.preparing-users"),
    FIGHT_CANT_ACT("fight.cant-act"),
    FIGHT_PLAYER_CHOSE("fight.player-chose"),
    ;

    private final String key;
}
