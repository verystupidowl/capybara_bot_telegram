package ru.tggc.telegrambotframework.exception;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import ru.tggc.telegrambotframework.dto.Response;

public interface ExceptionHandler {

    Response handleException(Exception e, Chat chat, User from);

    String buildMessageToAdmin(String s, Chat chat, User from);
}
