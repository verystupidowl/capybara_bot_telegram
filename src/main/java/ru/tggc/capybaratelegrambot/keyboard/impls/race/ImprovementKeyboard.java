package ru.tggc.capybaratelegrambot.keyboard.impls.race;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.capybaratelegrambot.keyboard.KeyboardType.IMPROVEMENTS;

@Component
public class ImprovementKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public ImprovementKeyboard() {
        super(IMPROVEMENTS);
    }

    @Override
    public Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> rows(
                btn("Удобные ботиночки", "improve_boots"),
                btn("Вкусный арбуз", "improve_watermelon"),
                btn("Антипроигрыш", "improve_pill")
        );
    }
}
