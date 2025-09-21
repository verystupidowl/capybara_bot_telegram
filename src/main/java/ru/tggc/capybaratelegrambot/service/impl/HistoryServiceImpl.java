package ru.tggc.capybaratelegrambot.service.impl;

import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraHistoryDto;
import ru.tggc.capybaratelegrambot.service.HistoryService;
import ru.tggc.capybaratelegrambot.utils.HistoryType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class HistoryServiceImpl implements HistoryService {
    private final Map<CapybaraHistoryDto, HistoryType> capybaraHistory = new ConcurrentHashMap<>();

    @Override
    public void startChangeName(CapybaraHistoryDto dto) {
        if (!capybaraHistory.containsKey(dto)) {
            capybaraHistory.put(dto, HistoryType.CHANGE_NAME);
        }
    }

    @Override
    public Boolean isChangingName(CapybaraHistoryDto dto) {
        return capybaraHistory.get(dto) == HistoryType.CHANGE_NAME;
    }

    @Override
    public void endChangeName(CapybaraHistoryDto dto) {
        capybaraHistory.remove(dto);
    }

    @Override
    public void startChangePhoto(CapybaraHistoryDto dto) {
        if (!capybaraHistory.containsKey(dto)) {
            capybaraHistory.put(dto, HistoryType.CHANGE_PHOTO);
        }
    }

    @Override
    public Boolean isChangingPhoto(CapybaraHistoryDto dto) {
        return capybaraHistory.get(dto) == HistoryType.CHANGE_PHOTO;
    }
}
