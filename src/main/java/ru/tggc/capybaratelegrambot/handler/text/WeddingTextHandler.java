package ru.tggc.capybaratelegrambot.handler.text;

import com.pengrad.telegrambot.model.Message;
import ru.tggc.capybaratelegrambot.aop.MessageHandleRegistry;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.MessageHandle;
import ru.tggc.capybaratelegrambot.aop.annotation.params.Ctx;
import ru.tggc.capybaratelegrambot.aop.annotation.params.HandleParam;
import ru.tggc.capybaratelegrambot.aop.annotation.params.MessageParam;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.dto.RequestType;
import ru.tggc.capybaratelegrambot.sender.Sender;
import ru.tggc.capybaratelegrambot.service.RequestService;
import ru.tggc.capybaratelegrambot.service.factory.RequestServiceFactory;

@BotHandler
public class WeddingTextHandler extends TextHandler {
    private final RequestServiceFactory requestServiceFactory;

    protected WeddingTextHandler(Sender sender,
                                 MessageHandleRegistry messageHandleRegistry,
                                 RequestServiceFactory requestServiceFactory) {
        super(sender, messageHandleRegistry);
        this.requestServiceFactory = requestServiceFactory;
    }

    @MessageHandle("пожениться @${username}")
    public void challengeToWedding(@HandleParam("username") String username,
                                   @Ctx CapybaraContext ctx,
                                   @MessageParam Message message) {
        String targetUsername = getTargetUsername(username, message, ctx.chatId());
        RequestService requestService = requestServiceFactory.getRequestService(RequestType.WEDDING);
        requestService.sendRequest(targetUsername, ctx);
    }
}
