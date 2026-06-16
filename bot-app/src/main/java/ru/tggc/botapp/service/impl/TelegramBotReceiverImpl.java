package ru.tggc.botapp.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.tggc.telegrambotframework.router.TelegramUpdateRouter;
import ru.tggc.telegrambotframework.service.TelegramBotReceiver;
import ru.tggc.telegrambotframework.service.TelegramBotSender;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBotReceiverImpl implements TelegramBotReceiver {
    private final Cache<Integer, Boolean> cachedUpdates = Caffeine.newBuilder()
            .maximumSize(100_000)
            .expireAfterWrite(Duration.ofMinutes(5))
            .build();

    private final TelegramUpdateRouter router;
    private final TelegramBotSender sender;

    @Async
    @Override
    public void receiveUpdate(Update update) {
        if (isNew(update)) {
            Optional.ofNullable(router.route(update))
                    .ifPresent(sender::send);

        }
    }

    public boolean isNew(Update update) {
        Integer id = update.updateId();
        if (id == null) {
            return true;
        }

        Boolean exists = cachedUpdates.getIfPresent(id);
        if (exists != null) {
            return false;
        }

        cachedUpdates.put(id, true);
        return true;
    }
}
