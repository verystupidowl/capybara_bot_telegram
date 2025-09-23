package ru.tggc.capybaratelegrambot.exceptions;

public class CapybaraHasNoMoneyException extends CapybaraException {

    public CapybaraHasNoMoneyException() {
        super("ur capy has no money(");
    }

    public CapybaraHasNoMoneyException(String message, String chatId) {
        super(message, chatId);
    }
}
