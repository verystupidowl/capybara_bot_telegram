package ru.tggc.botapp.exceptions;

import lombok.Getter;
import ru.tggc.botapp.util.Text;

@Getter
public class CapybaraAlreadyExistsException extends CapybaraException {

    public CapybaraAlreadyExistsException() {
        super(Text.ALREADY_HAVE_CAPYBARA);
    }
}
