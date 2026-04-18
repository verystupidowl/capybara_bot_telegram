package ru.tggc.capybaratelegrambot.keyboard.impls.fight;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.capybaratelegrambot.keyboard.KeyboardType.MAYBE_START_FIGHT;

@Component
public class MaybeStartFightKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public MaybeStartFightKeyboard() {
        super(MAYBE_START_FIGHT);
    }

    @Override
    public Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> rows(
                btn("Да начинаем", "start_fight"),
                toMainMenuBtn("Нет я ссу босса")
        );
    }
}
