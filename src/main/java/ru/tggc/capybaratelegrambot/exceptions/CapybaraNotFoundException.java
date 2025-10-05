package ru.tggc.capybaratelegrambot.exceptions;

import lombok.Getter;
import ru.tggc.capybaratelegrambot.utils.Text;

@Getter
public class CapybaraNotFoundException extends CapybaraException {

    public CapybaraNotFoundException() {
        super(Text.DONT_HAVE_CAPYBARA);
    }
}
