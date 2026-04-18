package ru.tggc.capybaratelegrambot.keyboard.impls.common;

import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.keyboard.AbstractKeyboardCreator;

import java.util.function.Supplier;

import static ru.tggc.capybaratelegrambot.keyboard.KeyboardType.REPLY;

@Component
public class ReplyKeyboard extends AbstractKeyboardCreator<Void> {

    protected ReplyKeyboard() {
        super(REPLY);
    }

    @Override
    public Supplier<String[][]> getRowsSupplier() {
        return () -> new String[][]{
                {"Моя капибара"},
                {"Топ капибар"},
                {"Выкинуть бедную капибару"},
                {"Топ капибар"}
        };
    }
}
