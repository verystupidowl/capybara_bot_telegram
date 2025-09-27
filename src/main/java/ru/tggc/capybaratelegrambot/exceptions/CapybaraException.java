package ru.tggc.capybaratelegrambot.exceptions;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.Getter;

@Getter
public class CapybaraException extends RuntimeException {
    private final String messageToSend;
    private InlineKeyboardMarkup markup;

    public CapybaraException(String messageToSend) {
        super(messageToSend);
        this.messageToSend = messageToSend;
    }

    public CapybaraException(String messageToSend, InlineKeyboardMarkup markup) {
        super(messageToSend);
        this.messageToSend = messageToSend;
        this.markup = markup;
    }

    public CapybaraException(String message, String messageToSend) {
        super(message);
        this.messageToSend = messageToSend;
    }
}
