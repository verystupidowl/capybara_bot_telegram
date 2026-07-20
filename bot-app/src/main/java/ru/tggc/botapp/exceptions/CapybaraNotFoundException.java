package ru.tggc.botapp.exceptions;

import lombok.Getter;
import ru.tggc.botapp.formatter.msgkey.ErrorMsgKey;

@Getter
public class CapybaraNotFoundException extends CapybaraException {

    public CapybaraNotFoundException() {
        super(ErrorMsgKey.CAPYBARA_NOT_FOUND);
    }
}
