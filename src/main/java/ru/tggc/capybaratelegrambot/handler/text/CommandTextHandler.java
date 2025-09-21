package ru.tggc.capybaratelegrambot.handler.text;

import com.pengrad.telegrambot.model.Message;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.MessageHandle;
import ru.tggc.capybaratelegrambot.aop.annotation.params.ChatId;
import ru.tggc.capybaratelegrambot.aop.annotation.params.HandleParam;
import ru.tggc.capybaratelegrambot.aop.annotation.params.MessageParam;
import ru.tggc.capybaratelegrambot.aop.annotation.params.UserId;
import ru.tggc.capybaratelegrambot.utils.CasinoTargetType;
import ru.tggc.capybaratelegrambot.service.CapybaraService;

@BotHandler
public class CommandTextHandler extends TextHandler {
    private final CapybaraService capybaraService;

    public CommandTextHandler(CapybaraService capybaraService) {
        this.capybaraService = capybaraService;
    }

    @MessageHandle("уволиться с работы")
    public void dismissal(@UserId String userId, @ChatId String chatId) {
        capybaraService.dismissal(userId, chatId);
        sendSimpleMessage(chatId, "ur capy has no work now", null);
    }

    @MessageHandle("казино ${count} ${target}")
    public void casino(@HandleParam("count") long count,
                       @HandleParam("target") String target,
                       @ChatId String chatId,
                       @UserId String userId) {
        CasinoTargetType type = CasinoTargetType.getByLabel(target);
        String response = capybaraService.casino(userId, chatId, count, type);

        sendSimpleMessage(chatId, response, null);
    }

    @MessageHandle("перевести дольки ${amount} ${username}")
    public void transferMoney(@HandleParam("amount") Long amount,
                              @HandleParam("username") String username,
                              @MessageParam Message message,
                              @ChatId String chatId,
                              @UserId String userId) {
        String targetUsername = getTargetUsername(username, message, chatId);
        capybaraService.transferMoney(userId, chatId, targetUsername, amount);

        sendSimpleMessage(chatId, "ok", null);
    }
}
