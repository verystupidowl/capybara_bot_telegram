package ru.tggc.capybaratelegrambot.handler.callback;

import com.pengrad.telegrambot.request.DeleteMessage;
import lombok.RequiredArgsConstructor;
import ru.tggc.capybaratelegrambot.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.annotation.handle.CallbackHandle;
import ru.tggc.capybaratelegrambot.annotation.params.ChatId;
import ru.tggc.capybaratelegrambot.annotation.params.Ctx;
import ru.tggc.capybaratelegrambot.annotation.params.HandleParam;
import ru.tggc.capybaratelegrambot.annotation.params.MessageId;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraInfoDto;
import ru.tggc.capybaratelegrambot.domain.dto.MyCapybaraDto;
import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.domain.response.Response;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.service.CapybaraService;
import ru.tggc.capybaratelegrambot.service.CasinoService;
import ru.tggc.capybaratelegrambot.service.HistoryService;
import ru.tggc.capybaratelegrambot.utils.CasinoTargetType;
import ru.tggc.capybaratelegrambot.utils.Text;
import ru.tggc.capybaratelegrambot.utils.TextBuilder;

import static ru.tggc.capybaratelegrambot.utils.HistoryType.CHANGE_NAME;
import static ru.tggc.capybaratelegrambot.utils.HistoryType.CHANGE_PHOTO;

@BotHandler
@RequiredArgsConstructor
public class CapybaraCallbackHandler extends CallbackHandler {
    private final HistoryService historyService;
    private final CapybaraService capybaraService;
    private final InlineKeyboardCreator inlineCreator;
    private final CasinoService casinoService;

    @CallbackHandle("set_name")
    public Response setName(@Ctx CapybaraContext ctx) {
        historyService.setHistory(ctx, CHANGE_NAME);
        return sendSimpleMessage(ctx.chatId(), Text.START_CHANGE_NAME, inlineCreator.notChange());
    }

    @CallbackHandle("set_photo")
    public Response setPhoto(@Ctx CapybaraContext ctx) {
        historyService.setHistory(ctx, CHANGE_PHOTO);
        return sendSimpleMessage(ctx.chatId(), Text.START_CHANGE_PHOTO, inlineCreator.notChange());
    }

    @CallbackHandle("exactly_delete")
    public Response deleteCapybara(@Ctx CapybaraContext ctx) {
        capybaraService.deleteCapybara(ctx);
        return sendSimpleMessage(ctx.chatId(), Text.DELETE_CAPYBARA);
    }

    @CallbackHandle("take_from_tea")
    public Response takeFromTea(@Ctx CapybaraContext ctx) {
        capybaraService.takeFromTea(ctx);
        return sendSimpleMessage(ctx.chatId(), "ok");
    }

    @CallbackHandle("go_tea")
    public Response goTea(@Ctx CapybaraContext ctx) {
        return sendSimplePhotos(capybaraService.goTea(ctx));
    }

    @CallbackHandle("fatten")
    public Response fatten(@Ctx CapybaraContext ctx) {
        return editPhotos(ctx.messageId(), capybaraService.fatten(ctx));
    }

    @CallbackHandle("feed")
    public Response feed(@Ctx CapybaraContext ctx) {
        return editPhotos(ctx.messageId(), capybaraService.feed(ctx));
    }

    @CallbackHandle("make_happy")
    public Response makeHappy(@Ctx CapybaraContext ctx) {
        return editPhotos(ctx.messageId(), capybaraService.makeHappy(ctx));
    }

    @CallbackHandle("feed_fatten")
    public Response feedFatten(@MessageId int messageId, @ChatId long chatId) {
        return editMessageCaption(chatId, messageId, Text.FEED_FATTEN, inlineCreator.feedKeyboard());
    }

    @CallbackHandle("set_default_photo")
    public Response setDefaultPhoto(@Ctx CapybaraContext ctx) {
        String response = capybaraService.setDefaultPhoto(ctx);
        return editSimpleMessage(ctx.chatId(), ctx.messageId(), response);
    }

    @CallbackHandle("not_change")
    public Response notChange(@Ctx CapybaraContext ctx) {
        historyService.removeFromHistory(ctx);
        return editSimpleMessage(ctx.chatId(), ctx.messageId(), "Ok");
    }

    @CallbackHandle("go_to_main")
    public Response sendGoToMainMessage(@Ctx CapybaraContext ctx) {
        MyCapybaraDto capybara = capybaraService.getMyCapybara(ctx);
        return editMessageCaption(
                ctx.chatId(),
                ctx.messageId(),
                TextBuilder.getMyCapybara(capybara),
                inlineCreator.myCapybaraKeyboard(capybara)
        );
    }

    @CallbackHandle("info")
    public Response sendInfoMessage(@Ctx CapybaraContext ctx) {
        CapybaraInfoDto info = capybaraService.getInfo(ctx);
        return editMessageCaption(
                ctx.chatId(),
                ctx.messageId(),
                Text.getInfo(info),
                inlineCreator.infoKeyboard(info)
        );
    }

    @CallbackHandle("casino_${target}")
    public Response casino(@Ctx CapybaraContext ctx,
                           @HandleParam("target") CasinoTargetType target) {
        PhotoDto response = casinoService.casino(ctx, target);
        return editPhoto(ctx.chatId(), ctx.messageId(), response.getUrl(), response.getCaption());
    }

    @CallbackHandle("take_capybara")
    public Response takeCapybara(@Ctx CapybaraContext ctx) {
        PhotoDto photoDto = capybaraService.saveCapybara(ctx);
        return sendSimplePhoto(photoDto)
                .andThen(Response.of(new DeleteMessage(ctx.chatId(), ctx.messageId())));
    }
}
