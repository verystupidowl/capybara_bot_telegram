package ru.tggc.capybaratelegrambot.keyboard;

import com.pengrad.telegrambot.model.request.Keyboard;

public interface KeyboardCreator<T, K extends Keyboard> {

    K create(T t);

    KeyboardType getKeyboardType();
}
