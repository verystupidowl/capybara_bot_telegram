package ru.tggc.capybaratelegrambot.handler.callback;

import lombok.RequiredArgsConstructor;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.CallbackHandle;
import ru.tggc.capybaratelegrambot.aop.annotation.params.Ctx;
import ru.tggc.capybaratelegrambot.aop.annotation.params.MessageId;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.domain.dto.response.Response;
import ru.tggc.capybaratelegrambot.service.WeddingService;

@BotHandler
@RequiredArgsConstructor
public class WeddingHandler extends CallbackHandler {
    private final WeddingService weddingService;

    @CallbackHandle("accept_wedding")
    public Response acceptWedding(@Ctx CapybaraContext ctx) {
        PhotoDto response = weddingService.respondWedding(ctx, true);
        return sendSimplePhoto(response);
    }

    @CallbackHandle("accept_unwedding")
    public Response unwedding(@Ctx CapybaraContext ctx,
                          @MessageId int messageId) {
        String message = weddingService.respondUnWedding(ctx, true);
        return editSimpleMessage(ctx.chatId(), messageId, message);
    }

    @CallbackHandle("refuse_wedding")
    public Response refuseWedding(@Ctx CapybaraContext ctx) {
        PhotoDto response = weddingService.respondWedding(ctx, false);
        return sendSimplePhoto(response);
    }

    @CallbackHandle("refuse_unwedding")
    public Response refuseUnwedding(@Ctx CapybaraContext ctx,
                                @MessageId int messageId) {
        String message = weddingService.respondUnWedding(ctx, false);
        return editSimpleMessage(ctx.chatId(), messageId, message);
    }
}
