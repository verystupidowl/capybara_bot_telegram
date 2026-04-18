package ru.tggc.capybaratelegrambot.keyboard.impls.work;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.capybaratelegrambot.keyboard.KeyboardType.BIG_IT_PROJECT;

@Component
public class BigItProjectKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public BigItProjectKeyboard() {
        super(BIG_IT_PROJECT);
    }

    @Override
    public Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> rows(
                btn("☕Банка кофе", "big_job_coffee"),
                btn("\uD83D\uDCDAКурсы по программированию", "big_job_courses"),
                btn("💰Мешок для денег", "big_job_bag"),
                btn("\uD83D\uDC4C\uD83C\uDFFBНичего", "big_job_nothing")
        );
    }
}
