package ru.tggc.botapp.handler.callback;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.DeleteMessage;
import ru.tggc.botapp.domain.dto.info.CapybaraInfoDto;
import ru.tggc.botapp.domain.dto.MyCapybaraDto;
import ru.tggc.botapp.formatter.common.CapybaraFormatter;
import ru.tggc.botapp.formatter.msgkey.CommonMsgKey;
import ru.tggc.botapp.keyboard.KeyboardFactory;
import ru.tggc.botapp.keyboard.KeyboardKey;
import ru.tggc.botapp.service.CapybaraService;
import ru.tggc.botapp.service.CasinoService;
import ru.tggc.botapp.service.impl.HistoryServiceImpl;
import ru.tggc.botapp.util.CasinoTargetType;
import ru.tggc.telegrambotcore.annotation.handle.BotHandler;
import ru.tggc.telegrambotcore.annotation.handle.CallbackHandle;
import ru.tggc.telegrambotcore.annotation.params.Ctx;
import ru.tggc.telegrambotcore.annotation.params.HandleParam;
import ru.tggc.telegrambotcore.dto.PhotoDto;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.formatter.FormatService;

import static ru.tggc.botapp.util.HistoryType.CHANGE_NAME;
import static ru.tggc.botapp.util.HistoryType.CHANGE_PHOTO;

@BotHandler
public record CapybaraCallbackHandler(HistoryServiceImpl historyService,
                                      CapybaraService capybaraService,
                                      KeyboardFactory keyboardFactory,
                                      CasinoService casinoService,
                                      CapybaraFormatter capybaraFormatter,
                                      FormatService formatService) {
    @CallbackHandle("set_name")
    public Response setName(@Ctx UpdateContext ctx) {
        historyService.setHistory(ctx, CHANGE_NAME);
        InlineKeyboardMarkup markup = keyboardFactory.getKeyboardInline(KeyboardKey.NOT_CHANGE);
        String message = formatService.get(CommonMsgKey.START_CHANGE_NAME);
        return ctx.send(message, markup);
    }

    @CallbackHandle("set_photo")
    public Response setPhoto(@Ctx UpdateContext ctx) {
        historyService.setHistory(ctx, CHANGE_PHOTO);
        InlineKeyboardMarkup markup = keyboardFactory.getKeyboardInline(KeyboardKey.DEFAULT_PHOTO);
        String message = formatService.get(CommonMsgKey.START_CHANGE_PHOTO);
        return ctx.send(message, markup);
    }

    @CallbackHandle("exactly_delete")
    public Response deleteCapybara(@Ctx UpdateContext ctx) {
        capybaraService.deleteCapybara(ctx);
        String message = formatService.get(CommonMsgKey.DELETED);
        return ctx.send(message);
    }

    @CallbackHandle("take_from_tea")
    public Response takeFromTea(@Ctx UpdateContext ctx) {
        capybaraService.takeFromTea(ctx);
        return ctx.send("ok");
    }

    @CallbackHandle("go_tea")
    public Response goTea(@Ctx UpdateContext ctx) {
        return ctx.send(capybaraService.goTea(ctx));
    }

    @CallbackHandle("fatten")
    public Response fatten(@Ctx UpdateContext ctx) {
        return ctx.edit(capybaraService.fatten(ctx));
    }

    @CallbackHandle("feed")
    public Response feed(@Ctx UpdateContext ctx) {
        return ctx.edit(capybaraService.feed(ctx));
    }

    @CallbackHandle("make_happy")
    public Response makeHappy(@Ctx UpdateContext ctx) {
        return ctx.edit(capybaraService.makeHappy(ctx));
    }

    @CallbackHandle("feed_fatten")
    public Response feedFatten(@Ctx UpdateContext ctx) {
        String message = formatService.get(CommonMsgKey.FEED_FATEN);
        return ctx.edit(message, keyboardFactory.getKeyboardInline(KeyboardKey.FEED));
    }

    @CallbackHandle("set_default_photo")
    public Response setDefaultPhoto(@Ctx UpdateContext ctx) {
        String response = capybaraService.setDefaultPhoto(ctx);
        return ctx.edit(response);
    }

    @CallbackHandle("not_change")
    public Response notChange(@Ctx UpdateContext ctx) {
        historyService.removeFromHistory(ctx);
        return ctx.edit("Ok");
    }

    @CallbackHandle("go_to_main")
    public Response sendGoToMainMessage(@Ctx UpdateContext ctx) {
        MyCapybaraDto capybara = capybaraService.getMyCapybara(ctx);
        return ctx.edit(
                capybaraFormatter.getMyCapybara(capybara),
                keyboardFactory.getKeyboardInline(KeyboardKey.MY_CAPYBARA, capybara)
        );
    }

    @CallbackHandle("info")
    public Response sendInfoMessage(@Ctx UpdateContext ctx) {
        CapybaraInfoDto info = capybaraService.getInfo(ctx);
        return ctx.edit(
                capybaraFormatter.getCapybaraInfo(info),
                keyboardFactory.getKeyboardInline(KeyboardKey.INFO, info)
        );
    }

    @CallbackHandle("casino_${target}")
    public Response casino(@Ctx UpdateContext ctx,
                           @HandleParam("target") CasinoTargetType target) {
        PhotoDto response = casinoService.casino(ctx, target);
        return ctx.edit(response.url(), response.caption());
    }

    @CallbackHandle("take_capybara")
    public Response takeCapybara(@Ctx UpdateContext ctx) {
        PhotoDto photoDto = capybaraService.saveCapybara(ctx);
        return ctx.send(photoDto)
                .andThen(Response.of(new DeleteMessage(ctx.chatId(), ctx.messageId())));
    }
}
