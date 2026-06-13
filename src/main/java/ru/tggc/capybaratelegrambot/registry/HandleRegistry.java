package ru.tggc.capybaratelegrambot.registry;

import com.pengrad.telegrambot.model.Update;
import ru.tggc.capybaratelegrambot.domain.response.Response;

public interface HandleRegistry {

    Response dispatch(Update update);

    boolean canHandle(Update update);
}
