package ru.tggc.capybaratelegrambot.keyboard.impls.common;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.capybaratelegrambot.keyboard.KeyboardType.NOT_CHANGE;

@Component
public class NotChangeKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public NotChangeKeyboard() {
        super(NOT_CHANGE);
    }

    @Override
    public Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> singleRow(btn("Не менять ничего", "not_change"));
    }
}
