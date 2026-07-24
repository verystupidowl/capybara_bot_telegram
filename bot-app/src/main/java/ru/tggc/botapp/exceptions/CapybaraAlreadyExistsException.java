package ru.tggc.botapp.exceptions;

import lombok.Getter;
import ru.tggc.botapp.formatter.msgkey.ErrorMsgKey;

@Getter
public class CapybaraAlreadyExistsException extends CapybaraException {

    public CapybaraAlreadyExistsException() {
        super(ErrorMsgKey.ALREADY_HAVE);
    }
}
