package ru.tggc.capybaratelegrambot.keyboard;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.Getter;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Getter
public abstract class AbstractInlineKeyboardCreator<T> implements KeyboardCreator<T, InlineKeyboardMarkup> {
    private final KeyboardType keyboardType;

    public AbstractInlineKeyboardCreator(KeyboardType keyboardType) {
        this.keyboardType = keyboardType;
    }

    protected Function<T, List<List<InlineKeyboardButton>>> getRowsFunction() {
        return null;
    }

    protected Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return null;
    }

    protected InlineKeyboardButton toMainMenuBtn(String text) {
        return new InlineKeyboardButton(text).callbackData("go_to_main");
    }

    protected List<List<InlineKeyboardButton>> singleBtn(InlineKeyboardButton button) {
        return List.of(List.of(button));
    }

    protected List<List<InlineKeyboardButton>> rows(InlineKeyboardButton... buttons) {
        return List.of(List.of(buttons));
    }

    protected InlineKeyboardButton btn(String text, String callbackData) {
        return new InlineKeyboardButton(text).callbackData(callbackData);
    }

    @Override
    public InlineKeyboardMarkup create(T data) {
        List<List<InlineKeyboardButton>> list;
        if (getRowsSupplier() != null) {
            list = getRowsSupplier().get();
        } else if (getRowsFunction() != null && data != null) {
            list = getRowsFunction().apply(data);
        } else {
            throw new CapybaraException("Логика кнопок не определена для " + keyboardType);
        }
        InlineKeyboardButton[][] rows = list.stream()
                .map(row -> row.toArray(InlineKeyboardButton[]::new))
                .toArray(InlineKeyboardButton[][]::new);
        return new InlineKeyboardMarkup(rows);
    }
}
