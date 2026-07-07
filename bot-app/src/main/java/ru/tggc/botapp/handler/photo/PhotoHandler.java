package ru.tggc.botapp.handler.photo;

import com.pengrad.telegrambot.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.tggc.telegrambotcore.handler.Handler;
import ru.tggc.botapp.service.CapybaraService;
import ru.tggc.botapp.service.impl.HistoryServiceImpl;
import ru.tggc.botapp.util.HistoryType;
import ru.tggc.telegrambotcore.annotation.handle.BotHandler;
import ru.tggc.telegrambotcore.annotation.handle.PhotoHandle;
import ru.tggc.telegrambotcore.annotation.params.Ctx;
import ru.tggc.telegrambotcore.annotation.params.MessageParam;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.UpdateContext;

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
