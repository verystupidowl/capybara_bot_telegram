package ru.tggc.capybaratelegrambot.handler.callback;

import ru.tggc.capybaratelegrambot.aop.CallbackRegistry;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.CallbackHandle;
import ru.tggc.capybaratelegrambot.aop.annotation.params.ChatId;
import ru.tggc.capybaratelegrambot.aop.annotation.params.Ctx;
import ru.tggc.capybaratelegrambot.aop.annotation.params.HandleParam;
import ru.tggc.capybaratelegrambot.aop.annotation.params.MessageId;
import ru.tggc.capybaratelegrambot.aop.annotation.params.UserId;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.model.enums.WorkType;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.sender.Sender;
import ru.tggc.capybaratelegrambot.service.impl.CapybaraService;

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
                              @Ctx CapybaraContext ctx) {
        capybaraService.takeFromWork(ctx)
                .forEach(text -> sendSimpleMessage(ctx.chatId(), messageId, text, null));

    }

    @CallbackHandle("go_job")
    private void goJob(@ChatId String chatId, @UserId String userId) {
        capybaraService.goJob(userId, chatId);
    }

    @CallbackHandle("set_job${jobType}")
    private void setJob(@MessageId int messageId,
                        @Ctx CapybaraContext ctx,
                        @HandleParam("jobType") WorkType workType) {
        capybaraService.setJob(ctx, workType);
        sendSimpleMessage(
                ctx.chatId(),
                messageId,
                "Твоя капибара теперь " + workType.getLabel() + "! Поздравляю!"
        );
    }

    @CallbackHandle("get_job")
    private void getJob(@MessageId int messageId,
                        @Ctx CapybaraContext ctx) {
        boolean hasWork = capybaraService.hasWork(ctx);
        if (!hasWork) {
            sendSimpleMessage(ctx.chatId(), messageId, "Выбери работу", inlineCreator.newJob());
        } else {
            sendSimpleMessage(ctx.chatId(), messageId, "Твоя капибара уже имеет работу", null);
        }
    }
}
