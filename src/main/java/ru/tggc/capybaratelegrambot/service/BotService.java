package ru.tggc.capybaratelegrambot.service;

import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.router.TelegramUpdateRouter;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BotService {
    private final TelegramUpdateRouter router;
    private final TelegramBotService telegramBotService;

    public void receiveUpdate(Update update) {
        Optional.ofNullable(router.route(update))
                .ifPresent(telegramBotService::send);
    }
}
