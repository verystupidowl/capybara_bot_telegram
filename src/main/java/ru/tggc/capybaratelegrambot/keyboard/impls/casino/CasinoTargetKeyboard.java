package ru.tggc.capybaratelegrambot.keyboard.impls.casino;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.capybaratelegrambot.keyboard.KeyboardType.CASINO_TARGET;

@Component
public class CasinoTargetKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public CasinoTargetKeyboard() {
        super(CASINO_TARGET);
    }

    @Override
    public Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> rows(
                btn("Красное", "casino_RED"),
                btn("Черное", "casino_BLACK"),
                btn("Зеро", "casino_ZERO")
        );
    }
}
