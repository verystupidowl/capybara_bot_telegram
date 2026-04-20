package ru.tggc.capybaratelegrambot.keyboard.impls.work;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.capybaratelegrambot.keyboard.KeyboardKey.CASH_REPORT;

@Component
public class CashReportImprovementKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public CashReportImprovementKeyboard() {
        super(CASH_REPORT);
    }

    @Override
    public Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> rows(
                btn("☕Банка кофе", "big_job_coffee"),
                btn("\uD83D\uDDA8Принтер", "big_job_printer"),
                btn("💰Мешок для денег", "big_job_bag"),
                btn("\uD83D\uDC4C\uD83C\uDFFBНичего", "big_job_nothing")
        );
    }
}
