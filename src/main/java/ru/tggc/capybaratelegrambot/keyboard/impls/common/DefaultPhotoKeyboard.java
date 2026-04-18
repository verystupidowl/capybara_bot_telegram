package ru.tggc.capybaratelegrambot.keyboard.impls.common;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.keyboard.AbstractInlineKeyboardCreator;

import java.util.List;
import java.util.function.Supplier;

import static ru.tggc.capybaratelegrambot.keyboard.KeyboardKey.DEFAULT_PHOTO;

@Component
public class DefaultPhotoKeyboard extends AbstractInlineKeyboardCreator<Void> {

    public DefaultPhotoKeyboard() {
        super(DEFAULT_PHOTO);
    }

    @Override
    public Supplier<List<List<InlineKeyboardButton>>> getRowsSupplier() {
        return () -> rows(
                btn("Не менять ничего", "not_change"),
                btn("Выбрать случайное фото", "set_default_photo")
        );
    }
}
