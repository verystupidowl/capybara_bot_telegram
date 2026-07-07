package ru.tggc.botapp.handler.callback;

import lombok.RequiredArgsConstructor;
import ru.tggc.botapp.service.WeddingService;
import ru.tggc.telegrambotcore.annotation.handle.BotHandler;
import ru.tggc.telegrambotcore.annotation.handle.CallbackHandle;
import ru.tggc.telegrambotcore.annotation.params.Ctx;
import ru.tggc.telegrambotcore.dto.PhotoDto;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.UpdateContext;

@BotHandler
@RequiredArgsConstructor
public class WeddingHandler extends CallbackHandler {
    private final WeddingService weddingService;

    @CallbackHandle("accept_wedding")
    public Response acceptWedding(@Ctx UpdateContext ctx) {
        PhotoDto response = weddingService.respondWedding(ctx, true);
        return sendSimplePhoto(response);
    }

    @CallbackHandle("accept_unwedding")
    public Response unwedding(@Ctx UpdateContext ctx) {
        String message = weddingService.respondUnWedding(ctx, true);
        return editSimpleMessage(ctx.chatId(), ctx.messageId(), message);
    }

    @CallbackHandle("refuse_wedding")
    public Response refuseWedding(@Ctx UpdateContext ctx) {
        PhotoDto response = weddingService.respondWedding(ctx, false);
        return sendSimplePhoto(response);
    }

    @CallbackHandle("refuse_unwedding")
    public Response refuseUnwedding(@Ctx UpdateContext ctx) {
        String message = weddingService.respondUnWedding(ctx, false);
        return editSimpleMessage(ctx.chatId(), ctx.messageId(), message);
    }
}
