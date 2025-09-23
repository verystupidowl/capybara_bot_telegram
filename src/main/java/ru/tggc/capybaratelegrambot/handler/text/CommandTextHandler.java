package ru.tggc.capybaratelegrambot.handler.text;

import com.pengrad.telegrambot.model.Message;
import ru.tggc.capybaratelegrambot.aop.MessageHandleRegistry;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.MessageHandle;
import ru.tggc.capybaratelegrambot.aop.annotation.params.Ctx;
import ru.tggc.capybaratelegrambot.aop.annotation.params.HandleParam;
import ru.tggc.capybaratelegrambot.aop.annotation.params.MessageParam;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.sender.Sender;
import ru.tggc.capybaratelegrambot.service.impl.CapybaraService;
import ru.tggc.capybaratelegrambot.service.impl.CasinoService;

@BotHandler
public class CommandTextHandler extends TextHandler {
    private final CapybaraService capybaraService;
    private final CasinoService casinoService;

    protected CommandTextHandler(Sender sender,
                                 MessageHandleRegistry messageHandleRegistry,
                                 CapybaraService capybaraService,
                                 CasinoService casinoService) {
        super(sender, messageHandleRegistry);
        this.capybaraService = capybaraService;
        this.casinoService = casinoService;
    }

    @MessageHandle("уволиться с работы")
    public void dismissal(@Ctx CapybaraContext ctx) {
        capybaraService.dismissal(ctx);
        sendSimpleMessage(ctx.chatId(), "ur capy has no work now", null);
    }

    @MessageHandle("казино")
    public void startCasino(@Ctx CapybaraContext ctx) {
        casinoService.startCasino(ctx);
        sendSimpleMessage(ctx.chatId(), "Введите ставку", null);
    }

    @MessageHandle("перевести дольки ${amount} ${username}")
    public void transferMoney(@HandleParam("amount") Long amount,
                              @HandleParam("username") String username,
                              @MessageParam Message message,
                              @Ctx CapybaraContext ctx) {
        String targetUsername = getTargetUsername(username, message, ctx.chatId());
        capybaraService.transferMoney(ctx, targetUsername, amount);

        sendSimpleMessage(ctx.chatId(), "ok", null);
    }
}
