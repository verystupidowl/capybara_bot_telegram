package ru.tggc.capybaratelegrambot.keyboard.impls.race;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.capybaratelegrambot.keyboard.KeyboardKey.RACE;

@Component
public class RaceKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public RaceKeyboard() {
        super(RACE);
    }

    @Override
    public Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> rows(
                btn("Принять забег", "accept_race"),
                btn("Отказаться от забега", "refuse_race")
        );
    }
}
