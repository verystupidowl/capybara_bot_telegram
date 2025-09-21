package ru.tggc.capybaratelegrambot.handler.text;

import com.pengrad.telegrambot.model.Message;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.MessageHandle;
import ru.tggc.capybaratelegrambot.aop.annotation.params.ChatId;
import ru.tggc.capybaratelegrambot.aop.annotation.params.HandleParam;
import ru.tggc.capybaratelegrambot.aop.annotation.params.MessageParam;
import ru.tggc.capybaratelegrambot.aop.annotation.params.UserId;
import ru.tggc.capybaratelegrambot.domain.dto.RequestType;
import ru.tggc.capybaratelegrambot.service.RequestService;
import ru.tggc.capybaratelegrambot.service.factory.RequestServiceFactory;

@BotHandler
public class RaceTextHandler extends TextHandler {
    private final RequestServiceFactory requestServiceFactory;

    public RaceTextHandler(RequestServiceFactory requestServiceFactory) {
        this.requestServiceFactory = requestServiceFactory;
    }

    @MessageHandle("забег @${username}")
    public void challengeToRace(@HandleParam("username") String username,
                                @UserId String userId,
                                @ChatId String chatId,
                                @MessageParam Message message) {
        String targetUsername = getTargetUsername(username, message, chatId);
        RequestService requestService = requestServiceFactory.getRequestService(RequestType.RACE);
        requestService.sendRequest(targetUsername, userId, chatId);
    }
}
