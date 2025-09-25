package ru.tggc.capybaratelegrambot.exceptions;

public class CapybaraNullException extends RuntimeException {

    public CapybaraNullException() {
    }

    public CapybaraNullException(String message) {
        super(message);
    }
}
