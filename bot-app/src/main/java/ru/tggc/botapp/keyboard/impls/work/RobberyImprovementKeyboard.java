package ru.tggc.botapp.keyboard.impls.work;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.botapp.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.botapp.keyboard.KeyboardKey.ROBBERY_IMPROVEMENT;

@Component
public class RobberyImprovementKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public RobberyImprovementKeyboard() {
        super(ROBBERY_IMPROVEMENT);
    }

    @Override
    public Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> rows(
                btn("\uD83E\uDD7EУдобные ботиночки", "big_job_boots"),
                btn("\uD83D\uDE97Быстрая машина", "big_job_car"),
                btn("💰Мешок для денег", "big_job_bag"),
                btn("\uD83D\uDC4C\uD83C\uDFFBНичего", "big_job_nothing")
        );
    }
}
