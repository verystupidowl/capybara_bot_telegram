package ru.tggc.capybaratelegrambot.keyboard;

import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;

public class SimpleKeyboardCreator {

    public ReplyKeyboardMarkup createMenuKeyboard() {
        return new ReplyKeyboardMarkup(new String[][]{
                {"Моя капибара"},
                {"Топ капибар"},
                {"Выкинуть бедную капибару"},
                {"Убрать клавиатуру"}
        }).resizeKeyboard(true);
    }
}
