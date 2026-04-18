package ru.tggc.capybaratelegrambot.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;
import ru.tggc.capybaratelegrambot.keyboard.KeyboardFactory;
import ru.tggc.capybaratelegrambot.keyboard.KeyboardType;
import ru.tggc.capybaratelegrambot.utils.HistoryType;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class HistoryService {
    private final Cache<CapybaraContext, HistoryType> capybaraHistory = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(3))
            .maximumSize(10_000)
            .build();
    private final KeyboardFactory keyboardFactory;

    public void setHistory(CapybaraContext dto, HistoryType type) {
        HistoryType prev = capybaraHistory.asMap().putIfAbsent(dto, type);
        if (prev != null) {
            throw new CapybaraException("ur capy already doing " + type, keyboardFactory.getKeyboardInline(KeyboardType.RACE));
        }
    }

    public boolean isInHistory(CapybaraContext dto, HistoryType type) {
        HistoryType historyType = capybaraHistory.getIfPresent(dto);
        return historyType != null && historyType == type;
    }

    public boolean contains(CapybaraContext dto) {
        return capybaraHistory.getIfPresent(dto) != null;
    }

    public void removeFromHistory(CapybaraContext dto) {
        capybaraHistory.invalidate(dto);
    }

    public HistoryType getFromHistory(CapybaraContext dto) {
        return capybaraHistory.getIfPresent(dto);
    }
}
