package ru.tggc.botapp.keyboard.impls.tea;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.telegrambotcore.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.botapp.keyboard.KeyboardType.TEA;
import static ru.tggc.botapp.util.KeyboardUtils.toMainMenuBtn;

@Component
public class TeaKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public TeaKeyboard() {
        super(TEA);
    }

    @Override
    public Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> rows(
                btn("Забрать капибару с чаепития", "take_from_tea"),
                toMainMenuBtn("Моя капибара")
        );
    }
}
