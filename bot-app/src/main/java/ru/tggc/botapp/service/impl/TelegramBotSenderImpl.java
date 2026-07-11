package ru.tggc.botapp.service.impl;

import com.pengrad.telegrambot.TelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.ResponseBuilder;
import ru.tggc.telegrambotcore.exception.RetryableException;
import ru.tggc.telegrambotcore.exception.RetryableWithSecsException;
import ru.tggc.telegrambotcore.service.TelegramBotSender;

import java.time.Instant;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBotSenderImpl implements TelegramBotSender {
    @Value("${telegram.admin-id}")
    private Long adminId;

    private final TelegramBot telegramBot;
    private final TaskScheduler taskScheduler;

    @Retryable(
            retryFor = RetryableWithSecsException.class,
            maxAttempts = 1,
            backoff = @Backoff(delay = 5000, multiplier = 2)
    )
    @Override
    public void send(@NotNull Response response) {
        try {
            response.accept(telegramBot).exceptionally(e -> {
                log.error("error", e);
                return null;
            });
        } catch (RetryableException e) {
            taskScheduler.schedule(
                    () -> send(response),
                    Instant.now().plusSeconds(e.getRetryMillis())
            );
        }
    }

    @Override
    public void sendToAdmin(@NotNull String text) {
        Response response = ResponseBuilder.to(adminId)
                .message(text, null)
                .build();
        response.accept(telegramBot);
    }

    @Override
    public void sendDelayed(@NotNull Consumer<TelegramBot> task, long delayMillis) {
        taskScheduler.schedule(
                () -> task.accept(telegramBot),
                Instant.now().plusMillis(delayMillis)
        );
    }

    @Recover
    public void recover(RetryableWithSecsException e, Response response) {
        sendToAdmin("Сообщение для пользователя не отправилось с ошибкой " + e.getMessage());
        log.error("Попытки отправить сообщение исчерпаны ", e);
    }
}
