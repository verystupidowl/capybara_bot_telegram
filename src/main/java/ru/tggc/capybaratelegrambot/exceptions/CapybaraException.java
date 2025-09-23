package ru.tggc.capybaratelegrambot.exceptions;

import lombok.Getter;

@Getter
public class CapybaraException extends RuntimeException {
    private String chatId;
    private String messageToSend;

    public CapybaraException(String messageToSend, String chatId) {
        this.chatId = chatId;
        this.messageToSend = messageToSend;
    }

    @Deprecated
    public CapybaraException(String message) {
        super(message);
    }

    @Deprecated
    public CapybaraException(String message, String chatId, String messageToSend) {
        super(message);
        this.chatId = chatId;
        this.messageToSend = messageToSend;
    }
}
