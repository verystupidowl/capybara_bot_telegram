package ru.tggc.botapp.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HistoryType {
    CHANGE_NAME("смена имени"),
    CASINO_SET_BET("внесение ставки казино"),
    CASINO_SET_TARGET("внесение цели казино"),
    SLOTS_SET_BET("внесение ставки слоты"),
    CHANGE_PHOTO("изменение фотографии"),
    BROADCAST("рассылка"),
    START_RACE("начало гонки"),
    ;

    private final String label;
}
