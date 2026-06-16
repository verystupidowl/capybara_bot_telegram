package ru.tggc.botapp.handler.photo;

import com.pengrad.telegrambot.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.tggc.telegrambotframework.handler.Handler;
import ru.tggc.botapp.service.CapybaraService;
import ru.tggc.botapp.service.impl.HistoryServiceImpl;
import ru.tggc.botapp.util.HistoryType;
import ru.tggc.telegrambotframework.annotation.handle.BotHandler;
import ru.tggc.telegrambotframework.annotation.handle.PhotoHandle;
import ru.tggc.telegrambotframework.annotation.params.Ctx;
import ru.tggc.telegrambotframework.annotation.params.MessageParam;
import ru.tggc.telegrambotframework.dto.Response;
import ru.tggc.telegrambotframework.dto.UpdateContext;

@Slf4j
@BotHandler
@RequiredArgsConstructor
public class PhotoHandler extends Handler {
    private final CapybaraService capybaraService;
    private final HistoryServiceImpl historyService;

    @PhotoHandle("update_photo")
    public Response updatePhoto(@Ctx UpdateContext ctx, @MessageParam Message message) {
        if (historyService.isInHistory(ctx, HistoryType.CHANGE_PHOTO)) {
            historyService.removeFromHistory(ctx);
            capybaraService.setPhoto(ctx, message);
            return sendSimpleMessage(ctx.chatId(), "Ты поменял фото своей капибары");
        }
        return null;
    }
}
