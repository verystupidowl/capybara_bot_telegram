package ru.tggc.capybaratelegrambot.service;

import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;
import ru.tggc.capybaratelegrambot.utils.HistoryType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class HistoryService {
    private final Map<CapybaraContext, HistoryType> capybaraHistory = new ConcurrentHashMap<>();

    public void setHistory(CapybaraContext dto, HistoryType type) {
        if (capybaraHistory.containsKey(dto)) {
            throw new CapybaraException("ur capy already doing " + type, dto.chatId());
        }
        capybaraHistory.putIfAbsent(dto, type);
    }

    public Boolean isInHistory(CapybaraContext dto) {
        return capybaraHistory.containsKey(dto);
    }

    public void removeFromHistory(CapybaraContext dto) {
        capybaraHistory.remove(dto);
    }

    public HistoryType getFromHistory(CapybaraContext dto) {
        return capybaraHistory.get(dto);
    }
}
