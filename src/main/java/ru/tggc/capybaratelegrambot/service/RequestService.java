package ru.tggc.capybaratelegrambot.service;

import ru.tggc.capybaratelegrambot.domain.dto.RequestType;

public interface RequestService {

    void sendRequest(String opponentUsername, String challengerId, String chatId);

    RequestType getRequestType();
}
