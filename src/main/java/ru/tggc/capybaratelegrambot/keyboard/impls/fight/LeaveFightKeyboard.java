package ru.tggc.capybaratelegrambot.keyboard.impls.fight;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.capybaratelegrambot.keyboard.KeyboardType.LEAVE_FIGHT;

@Component
public class LeaveFightKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public LeaveFightKeyboard() {
        super(LEAVE_FIGHT);
    }

    @Override
    public Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> rows(
                btn("Присоединиться к сражению", "join_fight"),
                btn("Ливнуть с позором", "leave_fight"),
                btn("Начать файт", "maybe_start_fight"),
                toMainMenuBtn("Назад")
        );
    }
}
