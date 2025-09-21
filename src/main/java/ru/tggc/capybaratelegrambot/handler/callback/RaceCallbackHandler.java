package ru.tggc.capybaratelegrambot.handler.callback;

import com.pengrad.telegrambot.model.CallbackQuery;
import ru.tggc.capybaratelegrambot.aop.CallbackRegistry;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.CallbackHandle;
import ru.tggc.capybaratelegrambot.aop.annotation.params.CallbackParam;
import ru.tggc.capybaratelegrambot.aop.annotation.params.ChatId;
import ru.tggc.capybaratelegrambot.aop.annotation.params.MessageId;
import ru.tggc.capybaratelegrambot.aop.annotation.params.UserId;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.enums.ImprovementValue;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.sender.Sender;
import ru.tggc.capybaratelegrambot.service.CapybaraService;

@BotHandler
public class RaceCallbackHandler extends CallbackHandler {
    private final CapybaraService capybaraService;
    private final InlineKeyboardCreator inlineCreator;

    public RaceCallbackHandler(CallbackRegistry callbackRegistry,
                               Sender sender,
                               CapybaraService capybaraService,
                               InlineKeyboardCreator inlineCreator) {
        super(sender, callbackRegistry);
        this.capybaraService = capybaraService;
        this.inlineCreator = inlineCreator;
    }

    @CallbackHandle("improve_pills")
    public void improvePills(@MessageId int messageId,
                             @ChatId String chatId,
                             @UserId String userId) {
        capybaraService.setImprovement(userId, chatId, ImprovementValue.ANTI_LOSE);
        sendSimpleMessage(chatId, messageId, "ur capy taken a pill", null);
    }

    @CallbackHandle("improve_watermelon")
    public void improveWatermelon(@MessageId int messageId,
                                  @ChatId String chatId,
                                  @UserId String userId) {
        capybaraService.setImprovement(userId, chatId, ImprovementValue.WATERMELON);
        sendSimpleMessage(chatId, messageId, "ur capy eaten a watermellon", null);
    }

    @CallbackHandle("improve_boots")
    private void improveBoots(@MessageId int messageId,
                              @ChatId String chatId,
                              @UserId String userId) {
        capybaraService.setImprovement(userId, chatId, ImprovementValue.BOOTS);
        sendSimpleMessage(chatId, messageId, "ur capy wears boots", null);
    }

    @CallbackHandle("buy_improve")
    private void buyImprove(@MessageId int messageId,
                            @ChatId String chatId,
                            @UserId String userId) {
        Capybara capybara = capybaraService.getCapybaraByUserId(userId, chatId);
        if (capybara.getImprovement().getImprovement() == ImprovementValue.NONE) {
            sendSimpleMessage(chatId, messageId, "choose one", inlineCreator.improvements());
        }
    }

    @CallbackHandle("do_massage")
    private void doMassage(@MessageId int messageId,
                           @ChatId String chatId,
                           @UserId String userId) {
        capybaraService.doMassage(userId, chatId);
        sendSimpleMessage(chatId, messageId, "u did a massge", null);
    }

    @CallbackHandle("refuse_race")
    private void refuseRace(@MessageId int messageId,
                            @ChatId String chatId,
                            @UserId String userId) {
        capybaraService.refuseRace(userId, chatId);
        sendSimpleMessage(chatId, messageId, "u refused a race", null);
    }

    @CallbackHandle("accept_race")
    private void acceptRace(@CallbackParam CallbackQuery query,
                            @ChatId String chatId,
                            @UserId String userId) {
        capybaraService.acceptRace(userId, chatId).accept(this, query);
    }
}
