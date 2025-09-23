package ru.tggc.capybaratelegrambot.handler.callback;

import ru.tggc.capybaratelegrambot.aop.CallbackRegistry;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.CallbackHandle;
import ru.tggc.capybaratelegrambot.aop.annotation.params.Ctx;
import ru.tggc.capybaratelegrambot.aop.annotation.params.MessageId;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.sender.Sender;
import ru.tggc.capybaratelegrambot.service.impl.WeddingService;

@BotHandler
public class WeddingHandler extends CallbackHandler {
    private final WeddingService weddingService;

    public WeddingHandler(Sender sender, CallbackRegistry callbackRegistry, WeddingService weddingService) {
        super(sender, callbackRegistry);
        this.weddingService = weddingService;
    }

    @CallbackHandle("accept_wedding")
    public void acceptWedding(@Ctx CapybaraContext ctx) {
        PhotoDto response = weddingService.respondWedding(ctx, true);
        sendSimplePhoto(response);
    }

    @CallbackHandle("accept_unwedding")
    public void unwedding(@Ctx CapybaraContext ctx,
                          @MessageId int messageId) {
        String message = weddingService.respondUnWedding(ctx, true);
        sendSimpleMessage(ctx.chatId(), messageId, message, null);
    }

    @CallbackHandle("refuse_wedding")
    public void refuseWedding(@Ctx CapybaraContext ctx) {
        PhotoDto response = weddingService.respondWedding(ctx, false);
        sendSimplePhoto(response);
    }

    @CallbackHandle("refuse_unwedding")
    public void refuseUnwedding(@Ctx CapybaraContext ctx,
                                @MessageId int messageId) {
        String message = weddingService.respondUnWedding(ctx, false);
        sendSimpleMessage(ctx.chatId(), messageId, message, null);
    }
}
