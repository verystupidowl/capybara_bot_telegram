package ru.tggc.capybaratelegrambot.exceptions;

import ru.tggc.capybaratelegrambot.utils.Text;

public class CapybaraHasNoMoneyException extends CapybaraException {

    public CapybaraHasNoMoneyException() {
        super(Text.NO_MONEY);
    }

}
