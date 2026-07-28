package ru.tggc.botapp.handler.text;

import com.pengrad.telegrambot.model.Message;
import ru.tggc.botapp.keyboard.KeyboardType;
import ru.tggc.botapp.service.CapybaraService;
import ru.tggc.botapp.service.CasinoService;
import ru.tggc.botapp.service.bossfight.BossFightService;
import ru.tggc.botapp.util.HandlerUtils;
import ru.tggc.botapp.util.HistoryType;
import ru.tggc.telegrambotcore.annotation.handle.BotHandler;
import ru.tggc.telegrambotcore.annotation.handle.MessageHandle;
import ru.tggc.telegrambotcore.annotation.params.Ctx;
import ru.tggc.telegrambotcore.annotation.params.HandleParam;
import ru.tggc.telegrambotcore.annotation.params.MessageParam;
import ru.tggc.telegrambotcore.annotation.params.Username;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.formatter.FormatService;
import ru.tggc.telegrambotcore.keyboard.KeyboardFactory;

@BotHandler
public record CommandTextHandler(CapybaraService capybaraService,
                                 CasinoService casinoService,
                                 BossFightService bossFightService,
                                 KeyboardFactory keyboardFactory,
                                 FormatService formatService) {
    @MessageHandle("уволиться с работы")
    public Response dismissal(@Ctx UpdateContext ctx) {
        capybaraService.dismissal(ctx);
        return ctx.send("Твоя капибара уволилась с работы");
    }

    @MessageHandle("казино")
    public Response startCasino(@Ctx UpdateContext ctx) {
        return ctx.ask(
                "Введите ставку",
                HistoryType.CASINO_SET_BET,
                keyboardFactory.getKeyboardInline(KeyboardType.CANCEL),
                HandlerUtils.fallback(formatService, keyboardFactory)
        );
    }

    @MessageHandle("слоты")
    public Response startSlots(@Ctx UpdateContext ctx) {
        return ctx.ask(
                "Введите ставку",
                HistoryType.SLOTS_SET_BET,
                keyboardFactory.getKeyboardInline(KeyboardType.CANCEL),
                HandlerUtils.fallback(formatService, keyboardFactory)
        );
    }

    @MessageHandle("перевести дольки ${amount} ${username}")
    public Response transferMoney(@HandleParam("amount") String amount,
                                  @HandleParam("username") String username,
                                  @MessageParam Message message,
                                  @Ctx UpdateContext ctx) {
        String targetUsername = HandlerUtils.getTargetUsername(username, message);
        Integer intAmount = Integer.parseInt(amount);
        capybaraService.transferMoney(ctx, targetUsername, intAmount); //todo доработать

        return ctx.send("ok");
    }

    @MessageHandle("test join")
    public Response test(@Ctx UpdateContext ctx, @Username String username) {
        bossFightService.joinFight(ctx, username);
        return ctx.send("Ты участвуешь теперь");
    }

    @MessageHandle("start fight")
    public Response startFight(@Ctx UpdateContext ctx, @Username String username) {
        return ctx.send(bossFightService.startFight(ctx.chatId()), keyboardFactory.getKeyboardInline(KeyboardType.FIGHT));
    }
}
