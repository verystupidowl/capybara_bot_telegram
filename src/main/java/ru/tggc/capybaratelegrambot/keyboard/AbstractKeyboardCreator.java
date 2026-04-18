package ru.tggc.capybaratelegrambot.keyboard;

import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import lombok.Getter;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;

import java.util.function.Function;
import java.util.function.Supplier;

@Getter
public abstract class AbstractKeyboardCreator<T> implements KeyboardCreator<T, ReplyKeyboardMarkup> {
    private final KeyboardType keyboardType;

    protected AbstractKeyboardCreator(KeyboardType keyboardType) {
        this.keyboardType = keyboardType;
    }

    public Function<T, String[][]> getRowsFunction() {
        return null;
    }

    public Supplier<String[][]> getRowsSupplier() {
        return null;
    }

    @Override
    public ReplyKeyboardMarkup create(T data) {
        String[][] rows;
        if (getRowsSupplier() != null) {
            rows = getRowsSupplier().get();
        } else if (getRowsFunction() != null) {
            rows = getRowsFunction().apply(data);
        } else {
            throw new CapybaraException("Логика кнопок не определена для " + keyboardType);
        }
        return new ReplyKeyboardMarkup(rows);
    }
}
