package ru.tggc.botapp.handler.callback;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.DeleteMessage;
import lombok.RequiredArgsConstructor;
import ru.tggc.botapp.domain.dto.CapybaraInfoDto;
import ru.tggc.botapp.domain.dto.MyCapybaraDto;
import ru.tggc.botapp.keyboard.KeyboardFactory;
import ru.tggc.botapp.keyboard.KeyboardKey;
import ru.tggc.botapp.service.CapybaraService;
import ru.tggc.botapp.service.CasinoService;
import ru.tggc.botapp.service.impl.HistoryServiceImpl;
import ru.tggc.botapp.util.CasinoTargetType;
import ru.tggc.botapp.util.Text;
import ru.tggc.botapp.util.TextBuilder;
import ru.tggc.telegrambotframework.annotation.handle.BotHandler;
import ru.tggc.telegrambotframework.annotation.handle.CallbackHandle;
import ru.tggc.telegrambotframework.annotation.params.ChatId;
import ru.tggc.telegrambotframework.annotation.params.Ctx;
import ru.tggc.telegrambotframework.annotation.params.HandleParam;
import ru.tggc.telegrambotframework.annotation.params.MessageId;
import ru.tggc.telegrambotframework.dto.PhotoDto;
import ru.tggc.telegrambotframework.dto.Response;
import ru.tggc.telegrambotframework.dto.UpdateContext;

import static ru.tggc.botapp.util.HistoryType.CHANGE_NAME;
import static ru.tggc.botapp.util.HistoryType.CHANGE_PHOTO;

@BotHandler
@RequiredArgsConstructor
public class CapybaraCallbackHandler extends CallbackHandler {
    private final HistoryServiceImpl historyService;
    private final CapybaraService capybaraService;
    private final KeyboardFactory keyboardFactory;
    private final CasinoService casinoService;

    @CallbackHandle("set_name")
    public Response setName(@Ctx UpdateContext ctx) {
        historyService.setHistory(ctx, CHANGE_NAME);
        InlineKeyboardMarkup markup = keyboardFactory.getKeyboardInline(KeyboardKey.NOT_CHANGE);
        return sendSimpleMessage(ctx.chatId(), Text.START_CHANGE_NAME, markup);
    }

    @CallbackHandle("set_photo")
    public Response setPhoto(@Ctx UpdateContext ctx) {
        historyService.setHistory(ctx, CHANGE_PHOTO);
        InlineKeyboardMarkup markup = keyboardFactory.getKeyboardInline(KeyboardKey.DEFAULT_PHOTO);
        return sendSimpleMessage(ctx.chatId(), Text.START_CHANGE_PHOTO, markup);
    }

    @CallbackHandle("exactly_delete")
    public Response deleteCapybara(@Ctx UpdateContext ctx) {
        capybaraService.deleteCapybara(ctx);
        return sendSimpleMessage(ctx.chatId(), Text.DELETE_CAPYBARA);
    }

    @CallbackHandle("take_from_tea")
    public Response takeFromTea(@Ctx UpdateContext ctx) {
        capybaraService.takeFromTea(ctx);
        return sendSimpleMessage(ctx.chatId(), "ok");
    }

    @CallbackHandle("go_tea")
    public Response goTea(@Ctx UpdateContext ctx) {
        return sendSimplePhotos(capybaraService.goTea(ctx));
    }

    @CallbackHandle("fatten")
    public Response fatten(@Ctx UpdateContext ctx) {
        return editPhotos(ctx.messageId(), capybaraService.fatten(ctx));
    }

    @CallbackHandle("feed")
    public Response feed(@Ctx UpdateContext ctx) {
        return editPhotos(ctx.messageId(), capybaraService.feed(ctx));
    }

    @CallbackHandle("make_happy")
    public Response makeHappy(@Ctx UpdateContext ctx) {
        return editPhotos(ctx.messageId(), capybaraService.makeHappy(ctx));
    }

    @CallbackHandle("feed_fatten")
    public Response feedFatten(@MessageId int messageId, @ChatId long chatId) {
        return editMessageCaption(chatId, messageId, Text.FEED_FATTEN, keyboardFactory.getKeyboardInline(KeyboardKey.FEED));
    }

    @CallbackHandle("set_default_photo")
    public Response setDefaultPhoto(@Ctx UpdateContext ctx) {
        String response = capybaraService.setDefaultPhoto(ctx);
        return editSimpleMessage(ctx.chatId(), ctx.messageId(), response);
    }

    @CallbackHandle("not_change")
    public Response notChange(@Ctx UpdateContext ctx) {
        historyService.removeFromHistory(ctx);
        return editSimpleMessage(ctx.chatId(), ctx.messageId(), "Ok");
    }

    @CallbackHandle("go_to_main")
    public Response sendGoToMainMessage(@Ctx UpdateContext ctx) {
        MyCapybaraDto capybara = capybaraService.getMyCapybara(ctx);
        return editMessageCaption(
                ctx.chatId(),
                ctx.messageId(),
                TextBuilder.getMyCapybara(capybara),
                keyboardFactory.getKeyboardInline(KeyboardKey.MY_CAPYBARA, capybara)
        );
    }

    @CallbackHandle("info")
    public Response sendInfoMessage(@Ctx UpdateContext ctx) {
        CapybaraInfoDto info = capybaraService.getInfo(ctx);
        return editMessageCaption(
                ctx.chatId(),
                ctx.messageId(),
                Text.getInfo(info),
                keyboardFactory.getKeyboardInline(KeyboardKey.INFO, info)
        );
    }

    @CallbackHandle("casino_${target}")
    public Response casino(@Ctx UpdateContext ctx,
                           @HandleParam("target") CasinoTargetType target) {
        PhotoDto response = casinoService.casino(ctx, target);
        return editPhoto(ctx.chatId(), ctx.messageId(), response.getUrl(), response.getCaption());
    }

    @CallbackHandle("take_capybara")
    public Response takeCapybara(@Ctx UpdateContext ctx) {
        PhotoDto photoDto = capybaraService.saveCapybara(ctx);
        return sendSimplePhoto(photoDto)
                .andThen(Response.of(new DeleteMessage(ctx.chatId(), ctx.messageId())));
    }
}
