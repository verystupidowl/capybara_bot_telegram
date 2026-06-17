package ru.tggc.botapp.formatter.msgkey;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CasinoMsgKey implements MsgKey {
    CASINO_CASINO_WIN("casino.casino.win"),
    CASINO_CASINO_LOSE("casino.casino.lose"),
    CASINO_SLOTS_WIN("casino.slots.win"),
    CASINO_SLOTS_LOSE("casino.slots.lose"),
    ;

    private final String key;
}
