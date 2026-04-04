package ru.tggc.capybaratelegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.domain.response.Response;
import ru.tggc.capybaratelegrambot.exceptions.SendException;
import ru.tggc.capybaratelegrambot.service.TelegramBotService;

import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramSendMessageListener implements Listener<TelegramBot> {
    private final TelegramBot telegramBot;
    private final TelegramBotService telegramBotService;

    @Async
    @EventListener
    @Retryable(
            retryFor = {SendException.class},
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void listen(Response response) {
        response.accept(telegramBot);
    }

    @Async
    @EventListener
    @Retryable(
            retryFor = {SendException.class},
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void listen(Consumer<TelegramBot> task) {
        task.accept(telegramBot);
    }

    @Recover
    public void recover(SendException e, Response response) {
        log.error("Попытки отправить сообщение исчерпаны ", e);
        telegramBotService.sendToAdmin("Попытки отправить сообщение исчерпаны " + e.getMessage());
    }
}
