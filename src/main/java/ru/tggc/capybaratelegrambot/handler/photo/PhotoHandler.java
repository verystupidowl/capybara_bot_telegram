package ru.tggc.capybaratelegrambot.handler.photo;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.PhotoSize;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.PhotoHandle;
import ru.tggc.capybaratelegrambot.aop.annotation.params.Ctx;
import ru.tggc.capybaratelegrambot.aop.annotation.params.MessageParam;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.dto.response.Response;
import ru.tggc.capybaratelegrambot.handler.Handler;
import ru.tggc.capybaratelegrambot.service.CapybaraService;
import ru.tggc.capybaratelegrambot.service.HistoryService;
import ru.tggc.capybaratelegrambot.utils.HistoryType;

@Slf4j
@BotHandler
@RequiredArgsConstructor
public class PhotoHandler extends Handler {
    private final CapybaraService capybaraService;
    private final HistoryService historyService;

    @PhotoHandle("update_photo")
    public Response updatePhoto(@Ctx CapybaraContext ctx, @MessageParam Message message) {
        if (historyService.isInHistory(ctx, HistoryType.CHANGE_PHOTO)) {
            PhotoSize photoSize = message.photo()[0];
            historyService.removeFromHistory(ctx);
            capybaraService.setPhoto(ctx, photoSize);
            return sendSimpleMessage(ctx.chatId(), "Ты поменял фото своей капибары");
        }
        return null;
    }
}
