package ru.tggc.botapp.handler.text;

import com.pengrad.telegrambot.model.Message;
import ru.tggc.botapp.keyboard.KeyboardType;
import ru.tggc.botapp.service.AdminService;
import ru.tggc.botapp.service.CapybaraService;
import ru.tggc.botapp.service.CasinoService;
import ru.tggc.botapp.service.RaceService;
import ru.tggc.botapp.service.impl.HistoryServiceImpl;
import ru.tggc.botapp.util.HistoryType;
import ru.tggc.telegrambotcore.annotation.handle.BotHandler;
import ru.tggc.telegrambotcore.annotation.handle.DefaultMessageHandle;
import ru.tggc.telegrambotcore.annotation.params.MessageParam;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.keyboard.KeyboardFactory;

@BotHandler
public record DefaultMessageHandler(HistoryServiceImpl historyService,
                                    CasinoService casinoService,
                                    KeyboardFactory keyboardFactory,
                                    CapybaraService capybaraService,
                                    RaceService raceService,
                                    AdminService adminService) {
    @DefaultMessageHandle
    public Response handleDefaultMessages(@MessageParam Message message) {
        long chatId = message.chat().id();
        long userId = message.from().id();
        String text = message.text();
        UpdateContext ctx = new UpdateContext(chatId, userId, message.messageId());
        HistoryType historyType = historyService.getFromHistory(ctx);
        if (historyType == null) {
            return null;
        }

        return switch (historyType) {
            case CASINO_SET_BET -> casinoSetBet(ctx, text);
            case CHANGE_NAME -> changeName(ctx, text);
            case SLOTS_SET_BET -> slots(ctx, text);
            case START_RACE -> race(ctx, text);
            case BROADCAST -> broadcast(ctx, text);
            default -> null;
        };
    }

    private Response broadcast(UpdateContext ctx, String text) {
        adminService.startBroadcast(ctx.chatId(), text);
        historyService.removeFromHistory(ctx);
        return ctx.send("Началось");
    }

    private Response race(UpdateContext ctx, String text) {
        if (!text.startsWith("@")) {
            return null;
        }
        String username = text.substring(1);
        raceService.sendRequest(username, ctx);
        historyService.removeFromHistory(ctx);
        return ctx.send(text + ", тебе бросили вызов!", keyboardFactory.getKeyboardInline(KeyboardType.RACE));
    }

    private Response slots(UpdateContext historyDto, String bet) {
        return casinoService.slots(historyDto, Long.parseLong(bet));
    }

    private Response changeName(UpdateContext ctx, String text) {
        capybaraService.changeName(ctx, text);
        historyService.removeFromHistory(ctx);
        return ctx.send("Твою капибару теперь зовут " + text + ", отличное имя!");
    }

    private Response casinoSetBet(UpdateContext ctx, String text) {
        return ctx.send(casinoService.setBet(ctx, text));
    }
}
