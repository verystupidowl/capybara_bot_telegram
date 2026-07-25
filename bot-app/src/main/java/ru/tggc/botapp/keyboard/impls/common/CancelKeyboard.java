package ru.tggc.botapp.keyboard.impls.common;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import ru.tggc.telegrambotcore.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.botapp.keyboard.KeyboardType.CANCEL;
import static ru.tggc.botapp.util.KeyboardUtils.toMainMenuBtn;

@Component
public class CancelKeyboard extends AbstractInlineKeyboardCreator<Void> {
    public CancelKeyboard() {
        super(CANCEL);
    }

    @Nullable
    @Override
    protected Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> singleBtn(toMainMenuBtn("Отменить"));
    }
}
