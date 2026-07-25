package ru.tggc.botapp.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tggc.botapp.exceptions.CapybaraException;
import ru.tggc.botapp.formatter.msgkey.CommonMsgKey;
import ru.tggc.botapp.keyboard.KeyboardType;
import ru.tggc.botapp.util.HistoryType;
import ru.tggc.telegrambotcore.dto.DialogSession;
import ru.tggc.telegrambotcore.dto.HistoryKey;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.formatter.FormatService;
import ru.tggc.telegrambotcore.keyboard.KeyboardFactory;
import ru.tggc.telegrambotcore.service.HistoryService;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {
    private final Cache<@NonNull UpdateContext, DialogSession> cache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(3))
            .maximumSize(10_000)
            .build();

    private final KeyboardFactory keyboardFactory;
    private final FormatService formatService;

    public void setHistory(@NonNull UpdateContext ctx,
                           @NonNull HistoryKey type,
                           @NonNull Consumer<DialogSession> failAction) {
        DialogSession prev = cache.asMap().putIfAbsent(ctx, new DialogSession(type, new HashMap<>()));
        if (prev != null) {
            failAction.accept(prev);
        }
    }

    public void setHistory(@NonNull UpdateContext ctx, @NonNull HistoryKey type) {
        setHistory(ctx, type, prev -> {
            throw new CapybaraException(
                    formatService.get(CommonMsgKey.ALREADY_DOING, prev.state().getLabel()),
                    keyboardFactory.getKeyboardInline(KeyboardType.NOT_CHANGE)
            );
        });
    }

    public void setHistory(@NonNull UpdateContext ctx,
                           @NonNull HistoryKey type,
                           @NonNull String key, @NonNull
                           String value) {
        setHistory(ctx, type);
        putData(ctx, key, value);
    }

    public void putData(@NonNull UpdateContext ctx, @NonNull String key, @NonNull String value) {
        Optional.ofNullable(cache.getIfPresent(ctx))
                .ifPresent(s -> s.data().put(key, value));
    }

    public boolean isEmpty(@NonNull UpdateContext ctx) {
        return Optional.ofNullable(cache.getIfPresent(ctx))
                .map(DialogSession::data)
                .map(Map::isEmpty)
                .orElse(true);
    }

    @NonNull
    public Optional<String> getData(@NonNull UpdateContext ctx, @NonNull String key) {
        return Optional.ofNullable(cache.getIfPresent(ctx))
                .map(s -> s.data().get(key));
    }

    public boolean isInHistory(@NonNull UpdateContext ctx, @NonNull HistoryKey type) {
        DialogSession session = cache.getIfPresent(ctx);
        return session != null && session.state() == type;
    }

    public boolean contains(UpdateContext ctx) {
        return cache.getIfPresent(ctx) != null;
    }

    public void removeFromHistory(@NonNull UpdateContext ctx) {
        cache.invalidate(ctx);
    }

    @Nullable
    public HistoryType getFromHistory(@NonNull UpdateContext ctx) {
        return Optional.ofNullable(cache.getIfPresent(ctx))
                .map(dialogSession -> (HistoryType) dialogSession.state())
                .orElse(null);
    }
}
