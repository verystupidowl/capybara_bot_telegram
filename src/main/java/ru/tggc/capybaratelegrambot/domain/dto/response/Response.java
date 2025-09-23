package ru.tggc.capybaratelegrambot.domain.dto.response;

import ru.tggc.capybaratelegrambot.domain.dto.ResponseType;

public interface Response {

    void setChatId(String chatId);

    ResponseType getResponseType();
}
