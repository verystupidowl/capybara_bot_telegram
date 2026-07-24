package ru.tggc.botapp.keyboard.impls.admin;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.telegrambotcore.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.botapp.keyboard.KeyboardType.ADMIN_MENU;

@Component
public class AdminMenuKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public AdminMenuKeyboard() {
        super(ADMIN_MENU);
    }

    @Override
    protected Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> rows(
                btn("Рассылка", "broadcast")
        );
    }
}
