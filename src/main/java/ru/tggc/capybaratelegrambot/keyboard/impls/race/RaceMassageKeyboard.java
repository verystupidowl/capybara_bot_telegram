package ru.tggc.capybaratelegrambot.keyboard.impls.race;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.capybaratelegrambot.keyboard.KeyboardType.RACE_MASSAGE;

@Component
public class RaceMassageKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public RaceMassageKeyboard() {
        super(RACE_MASSAGE);
    }

    @Override
    public Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> singleBtn(btn("Сделать массаж", "do_massage"));
    }
}
