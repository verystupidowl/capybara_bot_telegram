package ru.tggc.botapp.handler.text;

import com.pengrad.telegrambot.model.Message;
import ru.tggc.botapp.keyboard.KeyboardType;
import ru.tggc.botapp.service.AdminService;
import ru.tggc.botapp.service.CapybaraService;
import ru.tggc.botapp.service.CasinoService;
import ru.tggc.botapp.service.CommonService;
import ru.tggc.botapp.service.RaceService;
import ru.tggc.telegrambotcore.annotation.handle.BotHandler;
import ru.tggc.telegrambotcore.annotation.handle.TextHandle;
import ru.tggc.telegrambotcore.annotation.params.Ctx;
import ru.tggc.telegrambotcore.annotation.params.MessageParam;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.keyboard.KeyboardFactory;
import ru.tggc.telegrambotcore.service.HistoryService;

@BotHandler
public record DefaultMessageHandler(HistoryService historyService,
                                    CasinoService casinoService,
                                    KeyboardFactory keyboardFactory,
                                    CapybaraService capybaraService,
                                    RaceService raceService,
                                    AdminService adminService,
                                    CommonService commonService) {
    @TextHandle("BUG_REPORT")
    public Response bugReport(@Ctx UpdateContext ctx, @MessageParam Message message) {
        String text = message.text();
        return ctx.send(commonService.bugReport(ctx, text));
    }

    @TextHandle("BROADCAST")
    public Response broadcast(@Ctx UpdateContext ctx, @MessageParam Message message) {
        String text = message.text();
        adminService.startBroadcast(ctx.chatId(), text);
        return ctx.send("Началось");
    }

    @TextHandle("START_RACE")
    public Response race(@Ctx UpdateContext ctx, @MessageParam Message message) {
        String text = message.text();
        if (!text.startsWith("@")) {
            return null;
        }
        String username = text.substring(1);
        raceService.sendRequest(username, ctx);
        return ctx.send(text + ", тебе бросили вызов!", keyboardFactory.getKeyboardInline(KeyboardType.RACE));
    }

    @TextHandle("SLOTS_SET_BET")
    public Response slots(@Ctx UpdateContext ctx, @MessageParam Message message) {
        String text = message.text();
        return casinoService.slots(ctx, Long.parseLong(text));
    }

    @TextHandle("CHANGE_NAME")
    public Response changeName(@Ctx UpdateContext ctx, @MessageParam Message message) {
        String text = message.text();
        capybaraService.changeName(ctx, text);
        return ctx.send("Твою капибару теперь зовут " + text + ", отличное имя!", keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU));
    }

    @TextHandle(value = "CASINO_SET_BET", deleteAfterHandle = false)
    public Response casinoSetBet(@Ctx UpdateContext ctx, @MessageParam Message message) {
        String text = message.text();
        return ctx.sendWithDelete(casinoService.setBet(ctx, text))
                .andThen(ctx.cleanInput());
    }
}
