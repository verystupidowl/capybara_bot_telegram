package ru.tggc.capibaraBotTelegram.exceptions;

public class CapybaraHasNoMoneyException extends RuntimeException {

    public CapybaraHasNoMoneyException() {

    }

    public CapybaraHasNoMoneyException(String message) {
        super(message);
    }
}
