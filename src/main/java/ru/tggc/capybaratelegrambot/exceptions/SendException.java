package ru.tggc.capybaratelegrambot.exceptions;

public class SendException extends RetryableException {
    public SendException(String message) {
        super(message);
    }
}
