package ru.tggc.capybaratelegrambot.listener;

import java.util.function.Consumer;

public interface Listener<R> {

    void listen(Consumer<R> response);
}
