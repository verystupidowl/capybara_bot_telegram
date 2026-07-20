package ru.tggc.botapp.exceptions;

import ru.tggc.botapp.formatter.msgkey.ErrorMsgKey;

public class CapybaraHasNoMoneyException extends CapybaraException {

    public CapybaraHasNoMoneyException() {
        super(ErrorMsgKey.NO_MONEY);
    }

}
