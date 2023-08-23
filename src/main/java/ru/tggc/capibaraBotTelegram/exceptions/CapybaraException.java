package ru.tggc.capibaraBotTelegram.exceptions;

public class CapybaraException extends RuntimeException {

    public CapybaraException() {
    }

    public CapybaraException(String message) {
        super(message);
    }
}
