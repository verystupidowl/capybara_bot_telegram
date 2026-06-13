package ru.tggc.capybaratelegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.domain.response.Response;
import ru.tggc.capybaratelegrambot.domain.response.ResponseBuilder;
import ru.tggc.capybaratelegrambot.exceptions.RetryableException;
import ru.tggc.capybaratelegrambot.exceptions.RetryableWithSecsException;

import java.time.Instant;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBotService {
    private final TaskScheduler taskScheduler;
    private final TelegramBot telegramBot;

    @Retryable(
            retryFor = RetryableWithSecsException.class,
            maxAttempts = 1,
            backoff = @Backoff(delay = 5000, multiplier = 2)
    )
    public void send(Response response) {
        try {
            response.accept(telegramBot);
        } catch (RetryableException e) {
            taskScheduler.schedule(
                    () -> send(response),
                    Instant.now().plusSeconds(e.getRetryMillis())
            );
        }
    }

    public void sendToAdmin(String text) {
        Response response = ResponseBuilder.toAdmin()
                .message(text)
                .build();
        response.accept(telegramBot);
    }

    public void sendDelayed(Consumer<TelegramBot> task, long delayMillis) {
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
