package ru.tggc.botapp.exceptions;

import ru.tggc.botapp.util.Text;

public class CapybaraHasNoMoneyException extends CapybaraException {

    public CapybaraHasNoMoneyException() {
        super(Text.NO_MONEY);
    }

}
