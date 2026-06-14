package ru.tggc.botapp.handler.text;

import com.pengrad.telegrambot.model.Message;
import lombok.RequiredArgsConstructor;
import ru.tggc.botapp.domain.dto.RequestType;
import ru.tggc.botapp.keyboard.KeyboardFactory;
import ru.tggc.botapp.keyboard.KeyboardKey;
import ru.tggc.botapp.service.RequestService;
import ru.tggc.botapp.service.factory.RequestServiceFactory;
import ru.tggc.telegrambotframework.annotation.handle.BotHandler;
import ru.tggc.telegrambotframework.annotation.handle.MessageHandle;
import ru.tggc.telegrambotframework.annotation.params.Ctx;
import ru.tggc.telegrambotframework.annotation.params.HandleParam;
import ru.tggc.telegrambotframework.annotation.params.MessageParam;
import ru.tggc.telegrambotframework.dto.Response;
import ru.tggc.telegrambotframework.dto.UpdateContext;

@BotHandler
@RequiredArgsConstructor
public class RaceTextHandler extends TextHandler {
    private final RequestServiceFactory requestServiceFactory;
    private final KeyboardFactory keyboardFactory;

    @MessageHandle("забег")
    public Response challengeToRace(@HandleParam("username") String username,
                                    @Ctx UpdateContext ctx,
                                    @MessageParam Message message) {
        String targetUsername = getTargetUsername(username, message);
        RequestService requestService = requestServiceFactory.getRequestService(RequestType.RACE);
        requestService.sendRequest(targetUsername, ctx);
        return sendSimpleMessage(ctx.chatId(), "тебе бросили вызов!", keyboardFactory.getKeyboardInline(KeyboardKey.RACE));
    }
}
