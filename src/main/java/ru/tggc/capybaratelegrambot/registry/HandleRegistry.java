package ru.tggc.capybaratelegrambot.registry;

import ru.tggc.capybaratelegrambot.domain.response.Response;

public interface HandleRegistry<U> {

    Response dispatch(U p);
}
