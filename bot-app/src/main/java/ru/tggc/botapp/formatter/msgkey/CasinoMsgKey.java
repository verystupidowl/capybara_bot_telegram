package ru.tggc.botapp.formatter.msgkey;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.tggc.telegrambotcore.formatter.MsgKey;

@AllArgsConstructor
@Getter
public enum CasinoMsgKey implements MsgKey {
    CASINO_CASINO_WIN("casino.casino.win"),
    CASINO_CASINO_LOSE("casino.casino.lose"),
    CASINO_SLOTS_WIN("casino.slots.win"),
    CASINO_SLOTS_LOSE("casino.slots.lose"),
    CASINO_NOT_PLAYING("casino.error.not-playing"),
    CASINO_MIN_BET("casino.min-bet"),
    ;

    private final String key;
}
