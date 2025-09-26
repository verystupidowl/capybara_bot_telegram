package ru.tggc.capybaratelegrambot.exceptions;

import lombok.Getter;

@Getter
public class CapybaraNotFoundException extends CapybaraException {
    public CapybaraNotFoundException(String message) {
        super(message);
    }

    public CapybaraNotFoundException() {
        super(null);
    }
}
