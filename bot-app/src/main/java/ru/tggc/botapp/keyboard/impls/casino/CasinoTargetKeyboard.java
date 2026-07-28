package ru.tggc.botapp.keyboard.impls.casino;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.telegrambotcore.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.botapp.keyboard.KeyboardType.CASINO_TARGET;


@Component
public class CasinoTargetKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public CasinoTargetKeyboard() {
        super(CASINO_TARGET);
    }

    @Override
    public Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> rows(
                btn("Красное", "casino_target_RED"),
                btn("Черное", "casino_target_BLACK"),
                btn("Зеро", "casino_target_ZERO")
        );
    }
}
