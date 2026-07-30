package ru.tggc.botapp.keyboard.impls.casino;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import ru.tggc.telegrambotcore.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.botapp.keyboard.KeyboardType.CASINO_INFO;
import static ru.tggc.botapp.util.KeyboardUtils.toMainMenuBtn;

@Component
public class CasinoInfoKeyboard extends AbstractInlineKeyboardCreator<Void> {
    public CasinoInfoKeyboard() {
        super(CASINO_INFO);
    }

    @Nullable
    @Override
    protected Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> rows(
                btn("Слоты", "casino_slots"),
                btn("Казино", "casino_casino"),
                toMainMenuBtn()
        );
    }
}
