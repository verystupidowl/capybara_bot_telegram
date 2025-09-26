package ru.tggc.capybaratelegrambot.exceptions;

import lombok.Getter;

@Getter
public class CapybaraException extends RuntimeException {
    private final String messageToSend;

    public CapybaraException(String messageToSend) {
        super(messageToSend);
        this.messageToSend = messageToSend;
    }

    public CapybaraException(String message, String messageToSend) {
        super(message);
        this.messageToSend = messageToSend;
    }
}
