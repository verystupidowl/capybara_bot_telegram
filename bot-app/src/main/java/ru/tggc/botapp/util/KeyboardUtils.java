package ru.tggc.botapp.util;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import lombok.experimental.UtilityClass;

@UtilityClass
public class KeyboardUtils {

    public static InlineKeyboardButton toMainMenuBtn(String text) {
        return new InlineKeyboardButton(text).callbackData("go_to_main");
    }
}
