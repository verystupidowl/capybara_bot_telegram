package ru.tggc.capybaratelegrambot.exceptions;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

public class CapybaraTiredException extends CapybaraException {
    private static final String MESSAGE = "Твоя капибара устала! Подожди %s. Или сделай ей массаж";

    public CapybaraTiredException(String message) {
        super(MESSAGE.formatted(message));
    }

    public CapybaraTiredException(String message, InlineKeyboardMarkup markup) {
        super(MESSAGE.formatted(message), markup);
    }
}
