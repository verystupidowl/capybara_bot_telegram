package ru.tggc.capibaraBotTelegram.keyboard;

import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;

public class SimpleKeyboardCreator {

    public ReplyKeyboardMarkup createMenuKeyboard() {
        return new ReplyKeyboardMarkup(new String[][]{
                {"Моя капибара"},
                {"Выкинуть бедную капибару"},
                {"Топ капибар"}
        }).resizeKeyboard(true);
    }
}
