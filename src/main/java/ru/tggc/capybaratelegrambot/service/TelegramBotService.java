package ru.tggc.capybaratelegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.domain.response.Response;
import ru.tggc.capybaratelegrambot.domain.response.ResponseBuilder;

import java.time.Instant;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class TelegramBotService {
    private final ApplicationEventPublisher eventPublisher;
    private final TaskScheduler taskScheduler;

    public void send(Response response) {
        eventPublisher.publishEvent(response);
    }

    public void sendToAdmin(String text) {
        Response response = ResponseBuilder.toAdmin()
                .message(text)
                .build();
        eventPublisher.publishEvent(response);
    }

    public void sendDelayed(Response response, int delayMillis) {
        taskScheduler.schedule(
                () -> eventPublisher.publishEvent(response),
                Instant.now().plusMillis(delayMillis)
        );
    }

    public void sendDelayed(Consumer<TelegramBot> task, long delaySecs) {
        taskScheduler.schedule(
                () -> eventPublisher.publishEvent(task),
                Instant.now().plusMillis(delaySecs)
        );
    }
}
