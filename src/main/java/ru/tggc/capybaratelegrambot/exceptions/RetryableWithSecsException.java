package ru.tggc.capybaratelegrambot.exceptions;

public class RetryableWithSecsException extends RetryableException {

    public RetryableWithSecsException(String message) {
        super(message, 5);
    }
}
