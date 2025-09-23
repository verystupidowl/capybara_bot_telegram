package ru.tggc.capybaratelegrambot.handler.callback;

import com.pengrad.telegrambot.request.EditMessageCaption;
import ru.tggc.capybaratelegrambot.aop.CallbackRegistry;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.CallbackHandle;
import ru.tggc.capybaratelegrambot.aop.annotation.params.Ctx;
import ru.tggc.capybaratelegrambot.aop.annotation.params.HandleParam;
import ru.tggc.capybaratelegrambot.aop.annotation.params.MessageId;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraInfoDto;
import ru.tggc.capybaratelegrambot.domain.dto.MyCapybaraDto;
import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.mapper.CapybaraInfoMapper;
import ru.tggc.capybaratelegrambot.mapper.MyCapybaraMapper;
import ru.tggc.capybaratelegrambot.sender.Sender;
import ru.tggc.capybaratelegrambot.service.impl.CapybaraService;
import ru.tggc.capybaratelegrambot.service.impl.CasinoService;
import ru.tggc.capybaratelegrambot.service.impl.HistoryService;
import ru.tggc.capybaratelegrambot.utils.CasinoTargetType;
import ru.tggc.capybaratelegrambot.utils.Text;

import static ru.tggc.capybaratelegrambot.utils.HistoryType.CHANGE_NAME;
import static ru.tggc.capybaratelegrambot.utils.HistoryType.CHANGE_PHOTO;

@BotHandler
public class CapybaraCallbackHandler extends CallbackHandler {
    private final HistoryService historyService;
    private final CapybaraService capybaraService;
    private final InlineKeyboardCreator inlineCreator;
    private final CapybaraInfoMapper capybaraInfoMapper;
    private final MyCapybaraMapper myCapybaraMapper;
    private final CasinoService casinoService;

    public CapybaraCallbackHandler(CallbackRegistry callbackRegistry,
                                   Sender sender,
                                   HistoryService historyService,
                                   CapybaraService capybaraService,
                                   InlineKeyboardCreator inlineCreator,
                                   CapybaraInfoMapper capybaraInfoMapper,
                                   MyCapybaraMapper myCapybaraMapper,
                                   CasinoService casinoService) {
        super(sender, callbackRegistry);
        this.historyService = historyService;
        this.capybaraService = capybaraService;
        this.inlineCreator = inlineCreator;
        this.capybaraInfoMapper = capybaraInfoMapper;
        this.myCapybaraMapper = myCapybaraMapper;
        this.casinoService = casinoService;
    }

    @CallbackHandle("exactly_delete")
    public void deleteCapybara(@Ctx CapybaraContext ctx) {
        capybaraService.deleteCapybara(ctx);
    }

    @CallbackHandle("take_from_tea")
    private void takeFromTea(@MessageId int messageId,
                             @Ctx CapybaraContext ctx) {
        capybaraService.takeFromTea(ctx);
        sendSimpleMessage(ctx.chatId(), messageId, "ok", null);
    }

    @CallbackHandle("go_tea")
    private void goTea(@Ctx CapybaraContext ctx) {
        capybaraService.goTea(ctx)
                .forEach(this::sendSimplePhoto);
    }

    @CallbackHandle("fatten")
    private void fatten(@Ctx CapybaraContext ctx) {
        capybaraService.fatten(ctx).forEach(this::sendSimplePhoto);
    }

    @CallbackHandle("feed")
    private void feed(@Ctx CapybaraContext ctx) {
        capybaraService.feed(ctx).forEach(this::sendSimplePhoto);
    }

    @CallbackHandle("make_happy")
    private void makeHappy(@Ctx CapybaraContext ctx) {
        capybaraService.makeHappy(ctx).forEach(this::sendSimplePhoto);
    }

    @CallbackHandle("feed_fatten")
    private void feedFatten(@MessageId int messageId) {
        EditMessageCaption emc = new EditMessageCaption(String.valueOf(messageId));
        emc.caption(Text.FEED_FATTEN);
        emc.replyMarkup(inlineCreator.feedKeyboard());
        create(emc).send();
    }

    @CallbackHandle("set_default_photo")
    private void setDefaultPhoto(@MessageId int messageId,
                                 @Ctx CapybaraContext ctx) {
        String response = capybaraService.setDefaultPhoto(ctx);
        sendSimpleMessage(ctx.chatId(), messageId, response, null);
    }

    @CallbackHandle("not_change")
    private void notChange(@MessageId int messageId,
                           @Ctx CapybaraContext ctx) {
        historyService.removeFromHistory(ctx);
        sendSimpleMessage(ctx.chatId(), messageId, "Ok", null);
    }

    @CallbackHandle("start_change_photo")
    private void startChangePhoto(@MessageId int messageId,
                                  @Ctx CapybaraContext ctx) {
        historyService.setHistory(ctx, CHANGE_PHOTO);
        sendSimpleMessage(ctx.chatId(), messageId, Text.START_CHANGE_PHOTO, inlineCreator.defaultPhoto());
    }

    @CallbackHandle("start_change_name")
    private void startChangeName(@MessageId int messageId,
                                 @Ctx CapybaraContext ctx) {
        historyService.setHistory(ctx, CHANGE_NAME);
        sendSimpleMessage(ctx.chatId(), messageId, Text.START_CHANGE_NAME, inlineCreator.notChange());
    }

    @CallbackHandle("send_go_to_main_message")
    private void sendGoToMainMessage(@MessageId int messageId,
                                     @Ctx CapybaraContext ctx) {
        Capybara capybara = capybaraService.getCapybaraByContext(ctx);
        MyCapybaraDto dto = myCapybaraMapper.toDto(capybara);
        PhotoDto photoDto = PhotoDto.builder()
                .chatId(ctx.chatId())
                .caption(Text.getMyCapybara(dto))
                .markup(inlineCreator.myCapybaraKeyboard(capybara)) //todo))
                .build();
        sendSimplePhoto(photoDto);
    }

    @CallbackHandle("send_info_message")
    private void sendInfoMessage(@MessageId int messageId,
                                 @Ctx CapybaraContext ctx) {
        Capybara capybara = capybaraService.getCapybaraByContext(ctx);
        CapybaraInfoDto dto = capybaraInfoMapper.toDto(capybara);
        sendSimpleMessage(ctx.chatId(), messageId, Text.getInfo(dto), inlineCreator.infoKeyboard(dto));
    }

    @CallbackHandle("casino_${target}")
    private void casino(@MessageId int messageId,
                        @Ctx CapybaraContext ctx,
                        @HandleParam("target") CasinoTargetType target) {
        String response = casinoService.casino(ctx, target);
        sendSimpleMessage(ctx.chatId(), messageId, response);
    }
}
