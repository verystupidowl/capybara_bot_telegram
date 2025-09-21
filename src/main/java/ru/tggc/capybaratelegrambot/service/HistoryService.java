package ru.tggc.capybaratelegrambot.service;

import ru.tggc.capybaratelegrambot.domain.dto.CapybaraHistoryDto;

public interface HistoryService {

    void startChangeName(CapybaraHistoryDto dto);

    Boolean isChangingName(CapybaraHistoryDto dto);

    void endChangeName(CapybaraHistoryDto dto);

    void startChangePhoto(CapybaraHistoryDto dto);
    Boolean isChangingPhoto(CapybaraHistoryDto dto);
}
