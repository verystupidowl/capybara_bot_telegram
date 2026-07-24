package ru.tggc.botapp.keyboard.impls.fight;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.telegrambotcore.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.botapp.keyboard.KeyboardType.FIGHT_BUFF_TYPES;
import static ru.tggc.botapp.util.KeyboardUtils.toMainMenuBtn;

@Component
public class FightBuffTypesKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public FightBuffTypesKeyboard() {
        super(FIGHT_BUFF_TYPES);
    }

    @Override
    public Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> rows(
                btn("⚔️Атака", "fight_buffs_ATTACK"),
                btn("🛡Защита", "fight_buffs_DEFEND"),
                btn("🌿Хил", "fight_buffs_HEAL"),
                toMainMenuBtn("Ничего")
        );
    }
}
