package ru.tggc.capybaratelegrambot.handler.callback;

import ru.tggc.capybaratelegrambot.aop.CallbackRegistry;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.CallbackHandle;
import ru.tggc.capybaratelegrambot.aop.annotation.params.ChatId;
import ru.tggc.capybaratelegrambot.aop.annotation.params.HandleParam;
import ru.tggc.capybaratelegrambot.aop.annotation.params.MessageId;
import ru.tggc.capybaratelegrambot.aop.annotation.params.UserId;
import ru.tggc.capybaratelegrambot.domain.model.enums.JobType;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.sender.Sender;
import ru.tggc.capybaratelegrambot.service.CapybaraService;

@BotHandler
public class WorkCallbackHandler extends CallbackHandler {
    private final CapybaraService capybaraService;
    private final InlineKeyboardCreator inlineCreator;

    public WorkCallbackHandler(Sender sender,
                               CallbackRegistry callbackRegistry,
                               CapybaraService capybaraService,
                               InlineKeyboardCreator inlineCreator) {
        super(sender, callbackRegistry);
        this.capybaraService = capybaraService;
        this.inlineCreator = inlineCreator;
    }


    @CallbackHandle("take_from_work")
    private void takeFromWork(@MessageId int messageId,
                              @ChatId String chatId,
                              @UserId String userId) {
        capybaraService.takeFromWork(userId, chatId)
                .forEach(text -> sendSimpleMessage(chatId, messageId, text, null));

    }

    @CallbackHandle("go_job")
    private void goJob(@ChatId String chatId, @UserId String userId) {
        capybaraService.goJob(userId, chatId);
    }

    @CallbackHandle("set_job${jobType}")
    private void setJob(@MessageId int messageId,
                        @UserId String userId,
                        @ChatId String chatId,
                        @HandleParam("jobType") JobType jobType) {
        capybaraService.setJob(userId, chatId, jobType);
        sendSimpleMessage(
                chatId,
                messageId,
                "Твоя капибара теперь " + jobType.getLabel() + "! Поздравляю!",
                null
        );
    }

    @CallbackHandle("get_job")
    private void getJob(@MessageId int messageId,
                        @ChatId String chatId,
                        @UserId String userId) {
        boolean hasWork = capybaraService.hasWork(userId, chatId);
        if (!hasWork) {
            sendSimpleMessage(chatId, messageId, "Выбери работу", inlineCreator.newJob());
        } else {
            sendSimpleMessage(chatId, messageId, "Твоя капибара уже имеет работу", null);
        }
    }
}
