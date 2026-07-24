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

    BOSS_ACTION_TEMPLATE("fight.action.boss.action-template"),
    ;

    private final String key;
}
