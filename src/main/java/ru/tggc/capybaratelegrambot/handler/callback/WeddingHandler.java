package ru.tggc.capybaratelegrambot.handler.callback;

import ru.tggc.capybaratelegrambot.aop.CallbackRegistry;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.CallbackHandle;
import ru.tggc.capybaratelegrambot.aop.annotation.params.ChatId;
import ru.tggc.capybaratelegrambot.aop.annotation.params.MessageId;
import ru.tggc.capybaratelegrambot.aop.annotation.params.UserId;
import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.sender.Sender;
import ru.tggc.capybaratelegrambot.service.WeddingService;

@BotHandler
public class WeddingHandler extends CallbackHandler {
    private final WeddingService weddingService;

    public WeddingHandler(Sender sender, CallbackRegistry callbackRegistry, WeddingService weddingService) {
        super(sender, callbackRegistry);
        this.weddingService = weddingService;
    }

    @CallbackHandle("accept_wedding")
    public void acceptWedding(@ChatId String chatId,
                              @UserId String userId) {
        PhotoDto response = weddingService.respondWedding(userId, chatId, true);
        sendSimplePhoto(response);
    }

    @CallbackHandle("accept_unwedding")
    public void unwedding(@ChatId String chatId,
                          @UserId String userId,
                          @MessageId int messageId) {
        String message = weddingService.respondUnWedding(userId, chatId, true);
        sendSimpleMessage(chatId, messageId, message, null);
    }

    @CallbackHandle("refuse_wedding")
    public void refuseWedding(@ChatId String chatId,
                              @UserId String userId) {
        PhotoDto response = weddingService.respondWedding(userId, chatId, false);
        sendSimplePhoto(response);
    }

    @CallbackHandle("refuse_unwedding")
    public void refuseUnwedding(@ChatId String chatId,
                                @UserId String userId,
                                @MessageId int messageId) {
        String message = weddingService.respondUnWedding(userId, chatId, false);
        sendSimpleMessage(chatId, messageId, message, null);
    }
}
