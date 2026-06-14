package ru.tggc.botapp.keyboard.impls.common;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.botapp.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.botapp.keyboard.KeyboardKey.DELETE_CAPYBARA;

@Component
public class DeleteCapybaraKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public DeleteCapybaraKeyboard() {
        super(DELETE_CAPYBARA);
    }

    @Override
    public Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> singleBtn(btn("Точно выкинуть капибару", "exactly_delete"));
    }
}
