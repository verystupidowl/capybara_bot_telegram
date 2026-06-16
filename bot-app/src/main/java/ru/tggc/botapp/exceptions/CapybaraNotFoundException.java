package ru.tggc.botapp.exceptions;

import lombok.Getter;
import ru.tggc.botapp.util.Text;

@Getter
public class CapybaraNotFoundException extends CapybaraException {

    public CapybaraNotFoundException() {
        super(Text.DONT_HAVE_CAPYBARA);
    }
}
