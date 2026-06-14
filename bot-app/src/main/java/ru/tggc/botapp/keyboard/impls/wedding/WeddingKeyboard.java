package ru.tggc.botapp.keyboard.impls.wedding;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.botapp.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.botapp.keyboard.KeyboardKey.WEDDING;

@Component
public class WeddingKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public WeddingKeyboard() {
        super(WEDDING);
    }

    @Override
    public Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> rows(
                btn("Принять брак", "accept_wedding"),
                btn("Отказаться", "refuse_wedding")
        );
    }
}
