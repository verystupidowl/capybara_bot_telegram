package ru.tggc.botapp.formatter.msgkey;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.tggc.telegrambotcore.formatter.MsgKey;

@AllArgsConstructor
@Getter
public enum AdminMsgKey implements MsgKey {
    BROADCAST_START("admin.broadcast-start"),
    BROADCAST_ENDED("admin.broadcast-ended"),
    STATS("admin.stats"),
    BLOCK_MESSAGE("admin.block-message");

    private final String key;
}
