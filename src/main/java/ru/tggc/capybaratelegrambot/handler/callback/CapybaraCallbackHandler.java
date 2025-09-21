package ru.tggc.capybaratelegrambot.handler.callback;

import com.pengrad.telegrambot.request.EditMessageCaption;
import ru.tggc.capybaratelegrambot.aop.CallbackRegistry;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.CallbackHandle;
import ru.tggc.capybaratelegrambot.aop.annotation.params.ChatId;
import ru.tggc.capybaratelegrambot.aop.annotation.params.MessageId;
import ru.tggc.capybaratelegrambot.aop.annotation.params.UserId;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraHistoryDto;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraInfoDto;
import ru.tggc.capybaratelegrambot.domain.dto.MyCapybaraDto;
import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.mapper.CapybaraInfoMapper;
import ru.tggc.capybaratelegrambot.mapper.MyCapybaraMapper;
import ru.tggc.capybaratelegrambot.sender.Sender;
import ru.tggc.capybaratelegrambot.service.CapybaraService;
import ru.tggc.capybaratelegrambot.service.HistoryService;
import ru.tggc.capybaratelegrambot.utils.Text;

@BotHandler
public class CapybaraCallbackHandler extends CallbackHandler {
    private final HistoryService historyService;
    private final CapybaraService capybaraService;
    private final InlineKeyboardCreator inlineCreator;
    private final CapybaraInfoMapper capybaraInfoMapper;
    private final MyCapybaraMapper myCapybaraMapper;

    public CapybaraCallbackHandler(CallbackRegistry callbackRegistry,
                                   Sender sender,
                                   HistoryService historyService,
                                   CapybaraService capybaraService,
                                   InlineKeyboardCreator inlineCreator,
                                   CapybaraInfoMapper capybaraInfoMapper,
                                   MyCapybaraMapper myCapybaraMapper) {
        super(sender, callbackRegistry);
        this.historyService = historyService;
        this.capybaraService = capybaraService;
        this.inlineCreator = inlineCreator;
        this.capybaraInfoMapper = capybaraInfoMapper;
        this.myCapybaraMapper = myCapybaraMapper;
    }

    @CallbackHandle("exactly_delete")
    public void deleteCapybara(@UserId String userId, @ChatId String chatId) {
        capybaraService.deleteCapybara(userId, chatId);
    }

    @CallbackHandle("take_from_tea")
    private void takeFromTea(@MessageId int messageId,
                             @ChatId String chatId,
                             @UserId String userId) {
        capybaraService.takeFromTea(userId, chatId);
        sendSimpleMessage(chatId, messageId, "ok", null);
    }

    @CallbackHandle("go_tea")
    private void goTea(@ChatId String chatId,
                       @UserId String userId) {
        capybaraService.goTea(userId, chatId)
                .forEach(this::sendSimplePhoto);
    }

    @CallbackHandle("fatten")
    private void fatten(@ChatId String chatId,
                        @UserId String userId) {
        capybaraService.fatten(userId, chatId).forEach(this::sendSimplePhoto);
    }

    @CallbackHandle("feed")
    private void feed(@ChatId String chatId,
                      @UserId String userId) {
        capybaraService.feed(userId, chatId).forEach(this::sendSimplePhoto);
    }

    @CallbackHandle("make_happy")
    private void makeHappy(@ChatId String chatId,
                           @UserId String userId) {
        capybaraService.makeHappy(userId, chatId).forEach(this::sendSimplePhoto);
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
                                 @ChatId String chatId,
                                 @UserId String userId) {
        String response = capybaraService.setDefaultPhoto(userId, chatId);
        sendSimpleMessage(chatId, messageId, response, null);
    }

    @CallbackHandle("not_change")
    private void notChange(@MessageId int messageId,
                           @ChatId String chatId,
                           @UserId String userId) {
        CapybaraHistoryDto dto = new CapybaraHistoryDto(
                chatId,
                userId
        );
        historyService.endChangeName(dto);
        sendSimpleMessage(chatId, messageId, "Ok", null);
    }

    @CallbackHandle("start_change_photo")
    private void startChangePhoto(@MessageId int messageId,
                                  @ChatId String chatId,
                                  @UserId String userId) {
        CapybaraHistoryDto dto = new CapybaraHistoryDto(
                chatId,
                userId);
        historyService.startChangePhoto(dto);
        sendSimpleMessage(chatId, messageId, Text.START_CHANGE_PHOTO, inlineCreator.defaultPhoto());
    }

    @CallbackHandle("start_change_name")
    private void startChangeName(@MessageId int messageId,
                                 @ChatId String chatId,
                                 @UserId String userId) {
        CapybaraHistoryDto dto = new CapybaraHistoryDto(
                chatId,
                userId
        );
        historyService.startChangeName(dto);
        sendSimpleMessage(chatId, messageId, Text.START_CHANGE_NAME, inlineCreator.notChange());
    }

    @CallbackHandle("send_go_to_main_message")
    private void sendGoToMainMessage(@MessageId int messageId,
                                     @ChatId String chatId,
                                     @UserId String userId) {
        Capybara capybara = capybaraService.getCapybaraByUserId(userId, chatId);
        MyCapybaraDto dto = myCapybaraMapper.toDto(capybara);
        PhotoDto photoDto = PhotoDto.builder()
                .chatId(chatId)
                .caption(Text.getMyCapybara(dto))
                .markup(inlineCreator.myCapybaraKeyboard(capybara)) //todo))
                .build();
        sendSimplePhoto(photoDto);
    }

    @CallbackHandle("send_info_message")
    private void sendInfoMessage(@MessageId int messageId,
                                 @ChatId String chatId,
                                 @UserId String userId) {
        Capybara capybara = capybaraService.getCapybaraByUserId(userId, chatId);
        CapybaraInfoDto dto = capybaraInfoMapper.toDto(capybara);
        sendSimpleMessage(chatId, messageId, Text.getInfo(dto), inlineCreator.infoKeyboard(dto));
    }
}
