package ru.tggc.capybaratelegrambot.exceptions;

import lombok.Getter;

@Getter
public class CapybaraException extends RuntimeException {
    private String chatId;
    private String messageToSend;

    public CapybaraException() {
    }

    public CapybaraException(String message) {
        super(message);
    }

    public CapybaraException(String message, String chatId, String messageToSend) {
        super(message);
        this.chatId = chatId;
        this.messageToSend = messageToSend;
    }
}
