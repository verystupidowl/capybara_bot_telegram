package ru.tggc.botapp.keyboard.impls.race;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.telegrambotcore.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.botapp.keyboard.KeyboardType.IMPROVEMENTS;

@Component
public class ImprovementKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public ImprovementKeyboard() {
        super(IMPROVEMENTS);
    }

    @Override
    public Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> rows(
                btn("Удобные ботиночки", "improve_BOOTS"),
                btn("Вкусный арбуз", "improve_WATERMELON"),
                btn("Антипроигрыш", "improve_ANTI_LOSE")
        );
    }
}
