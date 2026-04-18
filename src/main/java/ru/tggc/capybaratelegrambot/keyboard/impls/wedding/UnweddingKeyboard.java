package ru.tggc.capybaratelegrambot.keyboard.impls.wedding;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.capybaratelegrambot.keyboard.KeyboardKey.UNWEDDING;

@Component
public class UnweddingKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public UnweddingKeyboard() {
        super(UNWEDDING);
    }

    @Override
    public Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> rows(
                btn("Забрать свои слова назад", "refuse_wedding"),
                btn("Подтвердить расторжение", "un_wedding")
        );
    }
}
