package ru.tggc.capybaratelegrambot.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.domain.response.Response;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class UserRateLimiterService {
    private final Cache<Long, Integer> countOfUpdates = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(10))
            .maximumSize(10_000)
            .build();
    private final Cache<Long, AtomicBoolean> lockCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(10))
            .maximumSize(10_000)
            .build();
    private static final int MAX_REQUESTS = 10;

    public void lock(long userId) {
        AtomicBoolean locked = lockCache.get(userId, id -> new AtomicBoolean(true));
        locked.set(true);
    }

    public void unlock(long userId) {
        AtomicBoolean locked = lockCache.getIfPresent(userId);
        if (locked != null) locked.set(false);
    }

    public Response checkUser(User from,
                              Chat chat) {
        log.info(lockCache.asMap().toString());
        AtomicBoolean locked = lockCache.get(from.id(), id -> new AtomicBoolean(false));
        if (locked.get()) {
            log.info("asd");
            return Response.empty();
        }
        Integer count = countOfUpdates.getIfPresent(from.id());
        if (count != null && count > MAX_REQUESTS) {
            log.info("user {} is trying to ddos", from.username());
            return countOfUpdates.policy().expireAfterWrite().map(ex -> {
                long chatId = chat.id();
                return ex.ageOf(from.id(), TimeUnit.SECONDS).stream().mapToObj(age -> {
                    String time = MAX_REQUESTS - age + "c";
                    String text = "Cлишком много запросов, попробуй снова через " + time;
                    return Response.of(new SendMessage(chatId, text));
                }).findFirst().orElseGet(() -> {
                    String text = "Cлишком много запросов";
                    return Response.of(new SendMessage(chatId, text));
                });
            }).orElseGet(Response::empty);
        }
        int currentCount = count == null ? 0 : count;
        countOfUpdates.put(from.id(), currentCount + 1);
        return null;
    }
}
