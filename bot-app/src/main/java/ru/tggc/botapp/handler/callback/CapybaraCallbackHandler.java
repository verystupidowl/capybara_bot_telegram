package ru.tggc.botapp.handler.callback;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import ru.tggc.botapp.exceptions.CapybaraException;
import ru.tggc.botapp.formatter.msgkey.CommonMsgKey;
import ru.tggc.botapp.keyboard.KeyboardType;
import ru.tggc.botapp.service.capybara.ServiceFacade;
import ru.tggc.telegrambotcore.annotation.handle.BotHandler;
import ru.tggc.telegrambotcore.annotation.handle.CallbackHandle;
import ru.tggc.telegrambotcore.annotation.params.Ctx;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.formatter.FormatService;
import ru.tggc.telegrambotcore.keyboard.KeyboardFactory;
import ru.tggc.telegrambotcore.service.HistoryService;

import static ru.tggc.botapp.util.HistoryType.CHANGE_NAME;
import static ru.tggc.botapp.util.HistoryType.CHANGE_PHOTO;

@BotHandler
public record CapybaraCallbackHandler(HistoryService historyService,
                                      ServiceFacade serviceFacade,
                                      KeyboardFactory keyboardFactory,
                                      FormatService formatService) {
    @CallbackHandle("set_name")
    public Response setName(@Ctx UpdateContext ctx) {
        InlineKeyboardMarkup markup = keyboardFactory.getKeyboardInline(KeyboardType.NOT_CHANGE);
        String message = formatService.get(CommonMsgKey.START_CHANGE_NAME);
        return ctx.ask(message, CHANGE_NAME, markup, prev -> {
            throw new CapybaraException(
                    formatService.get(CommonMsgKey.ALREADY_DOING, prev.state().getLabel()),
                    keyboardFactory.getKeyboardInline(KeyboardType.NOT_CHANGE)
            );
        });
    }

    @CallbackHandle("set_photo")
    public Response setPhoto(@Ctx UpdateContext ctx) {
        InlineKeyboardMarkup markup = keyboardFactory.getKeyboardInline(KeyboardType.DEFAULT_PHOTO);
        String message = formatService.get(CommonMsgKey.START_CHANGE_PHOTO);
        return ctx.ask(message, CHANGE_PHOTO, markup, prev -> {
            throw new CapybaraException(
                    formatService.get(CommonMsgKey.ALREADY_DOING, prev.state().getLabel()),
                    keyboardFactory.getKeyboardInline(KeyboardType.NOT_CHANGE)
            );
        });
    }

    @CallbackHandle("exactly_delete")
    public Response deleteCapybara(@Ctx UpdateContext ctx) {
        return serviceFacade.deleteCapybara(ctx);
    }

    @CallbackHandle("take_from_tea")
    public Response takeFromTea(@Ctx UpdateContext ctx) {
        return serviceFacade.takeFromTea(ctx);
    }

    @CallbackHandle("go_tea")
    public Response goTea(@Ctx UpdateContext ctx) {
        return serviceFacade.goTea(ctx);
    }

    @CallbackHandle("fatten")
    public Response fatten(@Ctx UpdateContext ctx) {
        return serviceFacade.fatten(ctx);
    }

    @CallbackHandle("feed")
    public Response feed(@Ctx UpdateContext ctx) {
        return serviceFacade.feed(ctx);
    }

    @CallbackHandle("make_happy")
    public Response makeHappy(@Ctx UpdateContext ctx) {
        return serviceFacade.makeHappy(ctx);
    }

    @CallbackHandle("feed_fatten")
    public Response feedFatten(@Ctx UpdateContext ctx) {
        String message = formatService.get(CommonMsgKey.FEED_FATEN);
        return ctx.edit(message, keyboardFactory.getKeyboardInline(KeyboardType.FEED));
    }

    @CallbackHandle("set_default_photo")
    public Response setDefaultPhoto(@Ctx UpdateContext ctx) {
        return serviceFacade.setDefaultPhoto(ctx);
    }

    @CallbackHandle("not_change")
    public Response notChange(@Ctx UpdateContext ctx) {
        historyService.removeFromHistory(ctx);
        return ctx.delete();
    }

    @CallbackHandle("go_to_main")
    public Response sendGoToMainMessage(@Ctx UpdateContext ctx) {
        return serviceFacade.getMyCapybara(ctx);
    }

    @CallbackHandle("info")
    public Response sendInfoMessage(@Ctx UpdateContext ctx) {
        return serviceFacade.getInfo(ctx);
    }

    @CallbackHandle("take_capybara")
    public Response takeCapybara(@Ctx UpdateContext ctx) {
        return serviceFacade.saveCapybara(ctx);
    }
}
