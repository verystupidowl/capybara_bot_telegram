package ru.tggc.capybaratelegrambot.keyboard.impls.common;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.capybaratelegrambot.keyboard.KeyboardType.TO_MAIN_MENU;

@Component
public class ToMainMenuKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public ToMainMenuKeyboard() {
        super(TO_MAIN_MENU);
    }

    @Override
    public Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> singleRow(toMainMenuBtn("Моя капибара"));
    }
}
