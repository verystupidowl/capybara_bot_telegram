package ru.tggc.telegrambotframework.router;

import com.pengrad.telegrambot.model.Update;
import ru.tggc.telegrambotframework.dto.Response;

public interface TelegramUpdateRouter {

    Response route(Update update);
}
