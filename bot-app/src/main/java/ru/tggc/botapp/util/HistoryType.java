package ru.tggc.botapp.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.tggc.telegrambotcore.dto.HistoryKey;

@Getter
@AllArgsConstructor
public enum HistoryType implements HistoryKey {
    CHANGE_NAME("смена имени"),
    CASINO_SET_BET("внесение ставки казино"),
    CASINO_SET_TARGET("внесение цели казино"),
    SLOTS_SET_BET("внесение ставки слоты"),
    CHANGE_PHOTO("изменение фотографии"),
    BROADCAST("рассылка"),
    START_RACE("начало гонки"),
    BUG_REPORT("Сообщение об ошибке");

    private final String label;
}
