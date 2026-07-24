package ru.tggc.botapp.handler.text;

import com.pengrad.telegrambot.model.Message;
import ru.tggc.botapp.domain.dto.RequestType;
import ru.tggc.botapp.keyboard.KeyboardType;
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
import ru.tggc.telegrambotcore.keyboard.KeyboardFactory;

@BotHandler
public record RaceTextHandler(RequestServiceFactory requestServiceFactory,
                              KeyboardFactory keyboardFactory) {
    @MessageHandle("забег")
    public Response challengeToRace(@HandleParam("username") String username,
                                    @Ctx UpdateContext ctx,
                                    @MessageParam Message message) {
        String targetUsername = HandlerUtils.getTargetUsername(username, message);
        RequestService requestService = requestServiceFactory.getRequestService(RequestType.RACE);
        requestService.sendRequest(targetUsername, ctx);
        return ctx.send("тебе бросили вызов!", keyboardFactory.getKeyboardInline(KeyboardType.RACE));
    }
}
