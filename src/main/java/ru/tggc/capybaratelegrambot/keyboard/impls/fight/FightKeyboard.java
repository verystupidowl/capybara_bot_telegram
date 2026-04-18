package ru.tggc.capybaratelegrambot.keyboard.impls.fight;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.capybaratelegrambot.keyboard.KeyboardType.FIGHT;

@Component
public class FightKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public FightKeyboard() {
        super(FIGHT);
    }

    @Override
    public Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> rows(
                btn("Атака", "fight_action_ATTACK"),
                btn("Хил", "fight_action_HEAL"),
                btn("Защита", "fight_action_DEFEND")
        );
    }
}
