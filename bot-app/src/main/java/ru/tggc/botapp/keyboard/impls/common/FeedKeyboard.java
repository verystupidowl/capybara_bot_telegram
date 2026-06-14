package ru.tggc.botapp.keyboard.impls.common;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.botapp.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.botapp.keyboard.KeyboardKey.FEED;

@Component
public class FeedKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public FeedKeyboard() {
        super(FEED);
    }

    @Override
    public Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> rows(
                btn("Покормить капибару", "feed"),
                btn("Откормить капибару", "fatten"),
                toMainMenuBtn("Не делать ничего")
        );
    }
}
