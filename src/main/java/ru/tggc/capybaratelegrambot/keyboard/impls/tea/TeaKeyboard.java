package ru.tggc.capybaratelegrambot.keyboard.impls.tea;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.capybaratelegrambot.keyboard.KeyboardKey.TEA;

@Component
public class TeaKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public TeaKeyboard() {
        super(TEA);
    }

    @Override
    public Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> singleBtn(btn("Забрать капибару с чаепития", "take_from_tea"));
    }
}
