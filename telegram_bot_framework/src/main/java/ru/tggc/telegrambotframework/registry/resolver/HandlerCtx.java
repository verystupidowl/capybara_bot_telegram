package ru.tggc.telegrambotframework.registry.resolver;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;

import java.util.regex.Matcher;

public record HandlerCtx(
        Update update,
        long chatId,
        User from,
        int messageId,
        Matcher matcher
) {
}
