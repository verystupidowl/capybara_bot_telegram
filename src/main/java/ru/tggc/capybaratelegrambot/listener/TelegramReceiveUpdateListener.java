package ru.tggc.capybaratelegrambot.listener;

import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.service.BotService;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramReceiveUpdateListener {
    private final BotService telegramBotService;

    @Async
    @EventListener
    public void listen(Update update) {
        telegramBotService.receiveUpdate(update);
    }
}
