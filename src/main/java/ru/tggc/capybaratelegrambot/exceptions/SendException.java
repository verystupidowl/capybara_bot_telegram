package ru.tggc.capybaratelegrambot.exceptions;

public class SendException extends RuntimeException {
    public SendException(String message) {
        super(message);
    }
}
