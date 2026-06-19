package ru.tggc.botapp.formatter.msgkey;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WorkMsgKey implements MsgKey {
    TAKE_FROM_WORK("work.take-from-work"),
    NEW_RISE("work.new-rise"),
    BUSTED("work.busted"),

    ERROR_HAS_NO_WORK("work.error.has-no-work"),
    ERROR_ALREADY_HAS_WORK("work.error.already-has-work");

    private final String key;
}
