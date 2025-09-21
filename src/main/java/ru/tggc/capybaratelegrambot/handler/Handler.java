package ru.tggc.capybaratelegrambot.handler;

public interface Handler<T> {
    void handle(T data);
}
