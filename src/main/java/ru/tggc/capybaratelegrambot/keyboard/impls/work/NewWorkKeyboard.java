package ru.tggc.capybaratelegrambot.keyboard.impls.work;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.capybaratelegrambot.keyboard.KeyboardType.NEW_WORK;

@Component
public class NewWorkKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public NewWorkKeyboard() {
        super(NEW_WORK);
    }

    @Override
    public Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> rows(
                btn("Программист\uD83D\uDC68\u200D\uD83D\uDCBB", "set_job_PROGRAMMING"),
                btn("Грабитель\uD83E\uDD77", "set_job_CRIMINAL"),
                btn("Кассир\uD83D\uDCB5", "set_job_CASHIER"),
                toMainMenuBtn("Моя капибара")
        );
    }
}
