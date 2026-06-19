package ru.tggc.botapp.formatter.msgkey;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AdminMsgKey implements MsgKey {
    ADMIN_BROADCAST_ENDED("admin.broadcast-ended"),
    ADMIN_STATS("admin.stats"),
    BLOCK_MESSAGE("admin.block-message");

    private final String key;
}
