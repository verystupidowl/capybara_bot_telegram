package ru.tggc.botapp.keyboard.impls.fight;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.botapp.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.botapp.keyboard.KeyboardKey.FIGHT;

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
