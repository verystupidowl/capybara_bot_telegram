package ru.tggc.botapp.handler.text;

import com.pengrad.telegrambot.model.Message;
import ru.tggc.botapp.domain.dto.RequestType;
import ru.tggc.botapp.keyboard.KeyboardFactory;
import ru.tggc.botapp.keyboard.KeyboardKey;
import ru.tggc.botapp.service.RequestService;
import ru.tggc.botapp.service.factory.RequestServiceFactory;
import ru.tggc.botapp.util.HandlerUtils;
import ru.tggc.telegrambotcore.annotation.handle.BotHandler;
import ru.tggc.telegrambotcore.annotation.handle.MessageHandle;
import ru.tggc.telegrambotcore.annotation.params.Ctx;
import ru.tggc.telegrambotcore.annotation.params.HandleParam;
import ru.tggc.telegrambotcore.annotation.params.MessageParam;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.UpdateContext;

@BotHandler
public record WeddingTextHandler(RequestServiceFactory requestServiceFactory, KeyboardFactory keyboardFactory) {
    @MessageHandle("пожениться")
    public Response challengeToWedding(@HandleParam("username") String username,
                                       @Ctx UpdateContext ctx,
                                       @MessageParam Message message) {
        String targetUsername = HandlerUtils.getTargetUsername(username, message);
        RequestService requestService = requestServiceFactory.getRequestService(RequestType.WEDDING);
        requestService.sendRequest(targetUsername, ctx);
        String text = "@" + username + ", тебе сделали предложение!";
        return ctx.send(text, keyboardFactory.getKeyboardInline(KeyboardKey.WEDDING));
    }
}
