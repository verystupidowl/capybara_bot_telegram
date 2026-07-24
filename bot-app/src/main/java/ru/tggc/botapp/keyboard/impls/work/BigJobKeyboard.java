package ru.tggc.botapp.keyboard.impls.work;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.telegrambotcore.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.botapp.keyboard.KeyboardType.BIG_JOB;

@Component
public class BigJobKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public BigJobKeyboard() {
        super(BIG_JOB);
    }

    @Override
    public Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> singleBtn(btn("Большое дело", "big_job"));
    }
}
