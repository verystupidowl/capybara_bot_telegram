package ru.tggc.botapp.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tggc.botapp.domain.dto.DialogSession;
import ru.tggc.botapp.exceptions.CapybaraException;
import ru.tggc.botapp.keyboard.KeyboardFactory;
import ru.tggc.botapp.keyboard.KeyboardKey;
import ru.tggc.botapp.util.HistoryType;
import ru.tggc.telegrambotframework.dto.UpdateContext;
import ru.tggc.telegrambotframework.service.HistoryService;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {
    private final Cache<UpdateContext, DialogSession> cache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(3))
            .maximumSize(10_000)
            .build();

    private final KeyboardFactory keyboardFactory;

    public void setHistory(UpdateContext ctx, HistoryType type, Consumer<DialogSession> failAction) {
        DialogSession prev = cache.asMap().putIfAbsent(ctx, new DialogSession(type, new HashMap<>()));
        if (prev != null) {
            failAction.accept(prev);
        }
    }

    public void setHistory(UpdateContext ctx, HistoryType type) {
        setHistory(ctx, type, prev -> {
            throw new CapybaraException("Ты уже делаешь " + prev.state().getLabel(), keyboardFactory.getKeyboardInline(KeyboardKey.NOT_CHANGE));
        });
    }

    public void setHistory(UpdateContext ctx, HistoryType type, String key, String value) {
        setHistory(ctx, type);
        putData(ctx, key, value);
    }

    public void putData(UpdateContext ctx, String key, String value) {
        Optional.ofNullable(cache.getIfPresent(ctx))
                .ifPresent(s -> s.data().put(key, value));
    }

    public boolean isEmpty(UpdateContext ctx) {
        return Optional.ofNullable(cache.getIfPresent(ctx))
                .map(DialogSession::data)
                .map(Map::isEmpty)
                .orElse(true);
    }

    public Optional<String> getData(UpdateContext ctx, String key) {
        return Optional.ofNullable(cache.getIfPresent(ctx))
                .map(s -> s.data().get(key));
    }

    public boolean isInHistory(UpdateContext ctx, HistoryType type) {
        DialogSession session = cache.getIfPresent(ctx);
        return session != null && session.state() == type;
    }

    public boolean contains(UpdateContext ctx) {
        return cache.getIfPresent(ctx) != null;
    }

    public void removeFromHistory(UpdateContext ctx) {
        cache.invalidate(ctx);
    }

    @Nullable
    public HistoryType getFromHistory(UpdateContext ctx) {
        return Optional.ofNullable(cache.getIfPresent(ctx))
                .map(DialogSession::state)
                .orElse(null);
    }
}
