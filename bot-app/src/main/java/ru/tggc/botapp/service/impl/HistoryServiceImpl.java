package ru.tggc.botapp.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tggc.botapp.exceptions.CapybaraException;
import ru.tggc.botapp.keyboard.KeyboardFactory;
import ru.tggc.botapp.keyboard.KeyboardKey;
import ru.tggc.botapp.util.HistoryType;
import ru.tggc.telegrambotframework.dto.UpdateContext;
import ru.tggc.telegrambotframework.service.HistoryService;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {
    private final Cache<UpdateContext, HistoryType> capybaraHistory = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(3))
            .maximumSize(10_000)
            .build();
    private final KeyboardFactory keyboardFactory;

    public void setHistory(UpdateContext dto, HistoryType type) {
        HistoryType prev = capybaraHistory.asMap().putIfAbsent(dto, type);
        if (prev != null) {
            throw new CapybaraException("ur capy already doing " + type, keyboardFactory.getKeyboardInline(KeyboardKey.RACE));
        }
    }

    public boolean isInHistory(UpdateContext dto, HistoryType type) {
        HistoryType historyType = capybaraHistory.getIfPresent(dto);
        return historyType != null && historyType == type;
    }

    public boolean contains(UpdateContext dto) {
        return capybaraHistory.getIfPresent(dto) != null;
    }

    public void removeFromHistory(UpdateContext dto) {
        capybaraHistory.invalidate(dto);
    }

    public HistoryType getFromHistory(UpdateContext dto) {
        return capybaraHistory.getIfPresent(dto);
    }
}
