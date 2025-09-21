package ru.tggc.capybaratelegrambot.mapper;

public interface Mappable<E, D> {
    D toDto(E e);
    E fromDto(D d);
}
