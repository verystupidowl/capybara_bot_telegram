package ru.tggc.botapp.handler.callback;

import ru.tggc.botapp.keyboard.KeyboardType;
import ru.tggc.botapp.service.CasinoService;
import ru.tggc.botapp.util.CasinoTargetType;
import ru.tggc.botapp.util.HandlerUtils;
import ru.tggc.botapp.util.HistoryType;
import ru.tggc.telegrambotcore.annotation.handle.BotHandler;
import ru.tggc.telegrambotcore.annotation.handle.CallbackHandle;
import ru.tggc.telegrambotcore.annotation.params.Ctx;
import ru.tggc.telegrambotcore.annotation.params.HandleParam;
import ru.tggc.telegrambotcore.dto.PhotoDto;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.formatter.FormatService;
import ru.tggc.telegrambotcore.keyboard.KeyboardFactory;

@BotHandler
public record CasinoCallbackHandler(
        CasinoService casinoService,
        KeyboardFactory keyboardFactory,
        FormatService formatService
) {
    @CallbackHandle("casino_info")
    public Response casinoInfo(@Ctx UpdateContext ctx) {
        PhotoDto photoDto = casinoService.getInfo(ctx);
        return ctx.edit(photoDto);
    }

    @CallbackHandle("casino_slots")
    public Response casinoSlots(@Ctx UpdateContext ctx) {
        return ctx.ask(
                "Введите ставку",
                HistoryType.SLOTS_SET_BET,
                keyboardFactory.getKeyboardInline(KeyboardType.CANCEL),
                HandlerUtils.fallback(formatService, keyboardFactory)
        ).andThen(ctx.delete());
    }

    @CallbackHandle("casino_casino")
    public Response casinoCasino(@Ctx UpdateContext ctx) {
        return ctx.ask(
                "Введите ставку",
                HistoryType.CASINO_SET_BET,
                keyboardFactory.getKeyboardInline(KeyboardType.CANCEL),
                HandlerUtils.fallback(formatService, keyboardFactory)
        ).andThen(ctx.delete());
    }


    @CallbackHandle("casino_target_${target}")
    public Response casino(@Ctx UpdateContext ctx,
                           @HandleParam("target") CasinoTargetType target) {
        return casinoService.casino(ctx, target)
                .andThen(ctx.cleanPromptAndInput());
    }
}
