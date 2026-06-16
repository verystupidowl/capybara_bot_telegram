package ru.tggc.botapp.keyboard;

import com.pengrad.telegrambot.model.request.Keyboard;

public interface KeyboardCreator<T, K extends Keyboard> {

    K create(T t);

    KeyboardKey<T> getKeyboardKey();
}
