package ru.tggc.botapp.keyboard.impls.fight;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.botapp.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.botapp.keyboard.KeyboardKey.MAYBE_START_FIGHT;

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
