package ru.tggc.capybaratelegrambot.service;

import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.dto.RequestType;

public interface RequestService {

    void sendRequest(String opponentUsername, CapybaraContext ctx);

    RequestType getRequestType();
}
