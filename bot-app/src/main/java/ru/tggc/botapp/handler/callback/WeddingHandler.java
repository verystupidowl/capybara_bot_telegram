package ru.tggc.botapp.handler.callback;

import ru.tggc.botapp.service.WeddingService;
import ru.tggc.telegrambotcore.annotation.handle.BotHandler;
import ru.tggc.telegrambotcore.annotation.handle.CallbackHandle;
import ru.tggc.telegrambotcore.annotation.params.Ctx;
import ru.tggc.telegrambotcore.dto.PhotoDto;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.UpdateContext;

@BotHandler
public record WeddingHandler(WeddingService weddingService) {
    @CallbackHandle("accept_wedding")
    public Response acceptWedding(@Ctx UpdateContext ctx) {
        PhotoDto response = weddingService.respondWedding(ctx, true);
        return ctx.send(response);
    }

    @CallbackHandle("accept_unwedding")
    public Response unwedding(@Ctx UpdateContext ctx) {
        String message = weddingService.respondUnWedding(ctx, true);
        return ctx.edit(message);
    }

    @CallbackHandle("refuse_wedding")
    public Response refuseWedding(@Ctx UpdateContext ctx) {
        PhotoDto response = weddingService.respondWedding(ctx, false);
        return ctx.send(response);
    }

    @CallbackHandle("refuse_unwedding")
    public Response refuseUnwedding(@Ctx UpdateContext ctx) {
        String message = weddingService.respondUnWedding(ctx, false);
        return ctx.edit(message);
    }

    @CallbackHandle("wedding_gift")
    public Response weddingGift(@Ctx UpdateContext ctx) {
        String message = weddingService.getWeddingGift(ctx);
        return ctx.send(message);
    }
}
