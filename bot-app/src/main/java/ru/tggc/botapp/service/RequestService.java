package ru.tggc.botapp.service;


import ru.tggc.botapp.domain.dto.RequestType;
import ru.tggc.telegrambotframework.dto.UpdateContext;

public interface RequestService {

    void sendRequest(String opponentUsername, UpdateContext ctx);

    RequestType getRequestType();
}
