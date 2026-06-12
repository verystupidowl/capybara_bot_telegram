package ru.tggc.capybaratelegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.domain.response.Response;
import ru.tggc.capybaratelegrambot.domain.response.ResponseBuilder;

import java.time.Instant;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class TelegramBotService {
    private final TaskScheduler taskScheduler;
    private final TelegramBot telegramBot;

    public void send(Response response) {
        response.accept(telegramBot);
    }

    public void sendToAdmin(String text) {
        Response response = ResponseBuilder.toAdmin()
                .message(text)
                .build();
        response.accept(telegramBot);
    }

    public void sendDelayed(Response response, int delayMillis) {
        taskScheduler.schedule(
                () -> response.accept(telegramBot),
                Instant.now().plusMillis(delayMillis)
        );
    }

    public void sendDelayed(Consumer<TelegramBot> task, long delayMillis) {
        taskScheduler.schedule(
                () -> task.accept(telegramBot),
                Instant.now().plusMillis(delayMillis)
        );
    }
}
