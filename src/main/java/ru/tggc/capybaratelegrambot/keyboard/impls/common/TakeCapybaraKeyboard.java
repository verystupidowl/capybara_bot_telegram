package ru.tggc.capybaratelegrambot.keyboard.impls.common;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.capybaratelegrambot.keyboard.KeyboardType.TAKE_CAPYBARA;

@Component
public class TakeCapybaraKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public TakeCapybaraKeyboard() {
        super(TAKE_CAPYBARA);
    }

    @Override
    public Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> singleBtn(btn("Взять капибару", "take_capybara"));
    }
}
