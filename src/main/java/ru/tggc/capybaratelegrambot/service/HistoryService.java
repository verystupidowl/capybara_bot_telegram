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
            throw new CapybaraException("ur capy already doing " + type);
        }
        capybaraHistory.putIfAbsent(dto, type);
    }

    public boolean isInHistory(CapybaraContext dto, HistoryType type) {
        HistoryType historyType = capybaraHistory.get(dto);
        return historyType != null && historyType == type;
    }

    public boolean contains(CapybaraContext ctx) {
        return capybaraHistory.containsKey(ctx);
    }

    public void removeFromHistory(CapybaraContext dto) {
        capybaraHistory.remove(dto);
    }

    public HistoryType getFromHistory(CapybaraContext dto) {
        return capybaraHistory.get(dto);
    }
}
