package ru.tggc.botapp.keyboard.impls.common;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.telegrambotcore.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.botapp.keyboard.KeyboardType.TO_MAIN_MENU;
import static ru.tggc.botapp.util.KeyboardUtils.toMainMenuBtn;

@Component
public class ToMainMenuKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public ToMainMenuKeyboard() {
        super(TO_MAIN_MENU);
    }

    @Override
    public Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> singleBtn(toMainMenuBtn("Моя капибара"));
    }
}
