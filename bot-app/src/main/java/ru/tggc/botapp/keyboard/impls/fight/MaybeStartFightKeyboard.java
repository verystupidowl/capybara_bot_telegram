package ru.tggc.botapp.keyboard.impls.fight;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.telegrambotcore.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.botapp.keyboard.KeyboardType.MAYBE_START_FIGHT;
import static ru.tggc.botapp.util.KeyboardUtils.toMainMenuBtn;

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
