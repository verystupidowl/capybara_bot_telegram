package ru.tggc.botapp.handler.text;

import com.pengrad.telegrambot.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import ru.tggc.botapp.keyboard.KeyboardFactory;
import ru.tggc.botapp.keyboard.KeyboardKey;
import ru.tggc.botapp.service.AdminService;
import ru.tggc.botapp.service.CapybaraService;
import ru.tggc.botapp.service.CasinoService;
import ru.tggc.botapp.service.RaceService;
import ru.tggc.botapp.service.impl.HistoryServiceImpl;
import ru.tggc.botapp.util.HistoryType;
import ru.tggc.telegrambotframework.annotation.handle.BotHandler;
import ru.tggc.telegrambotframework.annotation.handle.DefaultMessageHandle;
import ru.tggc.telegrambotframework.annotation.params.MessageParam;
import ru.tggc.telegrambotframework.dto.PhotoDto;
import ru.tggc.telegrambotframework.dto.Response;
import ru.tggc.telegrambotframework.dto.UpdateContext;

@BotHandler
@RequiredArgsConstructor
public class DefaultMessageHandler extends TextHandler {
    private final HistoryServiceImpl historyService;
    private final CasinoService casinoService;
    private final KeyboardFactory keyboardFactory;
    private final CapybaraService capybaraService;
    private final RaceService raceService;
    private final AdminService adminService;

    @Value("${bot.photos.casino.set-bet}")
    private String casinoSetBetPhoto;

    @DefaultMessageHandle
    public Response handleDefaultMessages(@MessageParam Message message) {
        long chatId = message.chat().id();
        long userId = message.from().id();
        String text = message.text();
        UpdateContext historyDto = new UpdateContext(chatId, userId, message.messageId());
        HistoryType historyType = historyService.getFromHistory(historyDto);
        if (historyType == null) {
            return null;
        }

        return switch (historyType) {
            case CASINO_SET_BET -> casinoSetBet(historyDto, text);
            case CHANGE_NAME -> changeName(historyDto, text);
            case SLOTS_SET_BET -> slots(historyDto, text);
            case START_RACE -> race(historyDto, text);
            case BROADCAST -> broadcast(historyDto, text);
            default -> null;
        };
    }

    private Response broadcast(UpdateContext historyDto, String text) {
        adminService.startBroadcast(historyDto.chatId(), text);
        historyService.removeFromHistory(historyDto);
        return sendSimpleMessage(historyDto.chatId(), "Началось");
    }

    private Response race(UpdateContext ctx, String text) {
        if (!text.startsWith("@")) {
            return null;
        }
        String username = text.substring(1);
        raceService.sendRequest(username, ctx);
        historyService.removeFromHistory(ctx);
        return sendSimpleMessage(ctx.chatId(), text + ", тебе бросили вызов!", keyboardFactory.getKeyboardInline(KeyboardKey.RACE));
    }

    private Response slots(UpdateContext historyDto, String bet) {
        return casinoService.slots(historyDto, Long.parseLong(bet));
    }

    private Response changeName(UpdateContext ctx, String text) {
        capybaraService.changeName(ctx, text);
        historyService.removeFromHistory(ctx);
        return sendSimpleMessage(ctx.chatId(), "Твою капибару теперь зовут " + text + ", отличное имя!");
    }

    private Response casinoSetBet(UpdateContext historyDto, String text) {
        casinoService.setBet(historyDto, text);
        PhotoDto photoDto = PhotoDto.builder()
                .caption("Введите цель")
                .url(casinoSetBetPhoto)
                .chatId(historyDto.chatId())
                .markup(keyboardFactory.getKeyboardInline(KeyboardKey.CASINO_TARGET))
                .build();
        return sendSimplePhoto(photoDto);
    }
}
