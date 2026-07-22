package ru.tggc.botapp.keyboard.impls.wedding;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.telegrambotcore.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.botapp.keyboard.KeyboardType.UNWEDDING;

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
