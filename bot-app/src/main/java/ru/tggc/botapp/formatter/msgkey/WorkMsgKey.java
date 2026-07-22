package ru.tggc.botapp.formatter.msgkey;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.tggc.telegrambotcore.formatter.MsgKey;

@Getter
@AllArgsConstructor
public enum WorkMsgKey implements MsgKey {
    TAKE_FROM_WORK("work.take-from-work"),
    NEW_RISE("work.new-rise"),
    BUSTED("work.busted"),
    NEW_WORK("work.new-work"),
    GO_WORK("work.go-work"),

    LIST_OF_THINGS_FOR_ROBBERY("work.list-of-things-for-robbery"),
    LIST_OF_THINGS_FOR_BIG_IT_PROJECT("work.list-of-things-for-big-it-project"),
    LIST_OF_THINGS_FOR_CASH_REPORT("work.list-of-things-for-cash-report"),

    ERROR_HAS_NO_WORK("work.error.has-no-work"),
    ERROR_ALREADY_HAS_WORK("work.error.already-has-work"),
    ;

    private final String key;
}
