package ru.tggc.capybaratelegrambot.exceptions;

import lombok.Getter;
import ru.tggc.capybaratelegrambot.utils.Text;

@Getter
public class CapybaraAlreadyExistsException extends CapybaraException {

    public CapybaraAlreadyExistsException() {
        super(Text.ALREADY_HAVE_CAPYBARA);
    }
}
