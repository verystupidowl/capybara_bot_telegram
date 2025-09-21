package ru.tggc.capybaratelegrambot.exceptions;

import lombok.Getter;

@Getter
public class CapybaraNotFoundException extends RuntimeException {
    private final String chatId;
    public CapybaraNotFoundException(String message, String chatId) {
        super(message);
        this.chatId = chatId;
    }
}
