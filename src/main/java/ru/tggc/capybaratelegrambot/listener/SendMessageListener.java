package ru.tggc.capybaratelegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.domain.response.Response;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class SendMessageListener {
    private final TelegramBot telegramBot;

    @Async
    @EventListener
    public void listen(Response response) {
        response.send(telegramBot);
    }

    @Async
    @EventListener
    public void listen(Consumer<TelegramBot> task) {
        task.accept(this.telegramBot);
    }
}
