package ru.tggc.capybaratelegrambot.handler.text;

import com.pengrad.telegrambot.model.Message;
import lombok.RequiredArgsConstructor;
import ru.tggc.capybaratelegrambot.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.annotation.handle.MessageHandle;
import ru.tggc.capybaratelegrambot.annotation.params.Ctx;
import ru.tggc.capybaratelegrambot.annotation.params.HandleParam;
import ru.tggc.capybaratelegrambot.annotation.params.MessageParam;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.dto.enums.RequestType;
import ru.tggc.capybaratelegrambot.domain.dto.response.Response;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.service.RequestService;
import ru.tggc.capybaratelegrambot.service.factory.RequestServiceFactory;

@BotHandler
@RequiredArgsConstructor
public class RaceTextHandler extends TextHandler {
    private final RequestServiceFactory requestServiceFactory;
    private final InlineKeyboardCreator inlineCreator;

    @MessageHandle("забег")
    public Response challengeToRace(@HandleParam("username") String username,
                                    @Ctx CapybaraContext ctx,
                                    @MessageParam Message message) {
        String targetUsername = getTargetUsername(username, message);
        RequestService requestService = requestServiceFactory.getRequestService(RequestType.RACE);
        requestService.sendRequest(targetUsername, ctx);
        return sendSimpleMessage(ctx.chatId(), "тебе бросили вызов!", inlineCreator.raceKeyboard());
    }
}
