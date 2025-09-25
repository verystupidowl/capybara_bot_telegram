package ru.tggc.capybaratelegrambot.exceptions;

import lombok.Getter;

@Getter
public class CapybaraAlreadyExistsException extends RuntimeException {
    private final String chatId;

    public CapybaraAlreadyExistsException(String message, String chatId) {
        super(message);
        this.chatId = chatId;
    }
}
