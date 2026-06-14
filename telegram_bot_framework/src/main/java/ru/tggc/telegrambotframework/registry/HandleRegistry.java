package ru.tggc.telegrambotframework.registry;

import com.pengrad.telegrambot.model.Update;
import ru.tggc.telegrambotframework.dto.Response;

public interface HandleRegistry {

    Response dispatch(Update update);

    boolean canHandle(Update update);
}
