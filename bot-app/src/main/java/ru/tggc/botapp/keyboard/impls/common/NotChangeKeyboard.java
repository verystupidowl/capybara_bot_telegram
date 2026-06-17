package ru.tggc.botapp.keyboard.impls.common;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.botapp.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.botapp.keyboard.KeyboardKey.NOT_CHANGE;

@Component
public class NotChangeKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public NotChangeKeyboard() {
        super(NOT_CHANGE);
    }

    @Override
    public Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> singleBtn(btn("Отменить", "not_change"));
    }
}
