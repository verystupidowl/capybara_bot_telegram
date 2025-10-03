package ru.tggc.capybaratelegrambot.handler.text;

import com.pengrad.telegrambot.model.Message;
import lombok.RequiredArgsConstructor;
import ru.tggc.capybaratelegrambot.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.annotation.handle.MessageHandle;
import ru.tggc.capybaratelegrambot.annotation.params.Ctx;
import ru.tggc.capybaratelegrambot.annotation.params.HandleParam;
import ru.tggc.capybaratelegrambot.annotation.params.MessageParam;
import ru.tggc.capybaratelegrambot.annotation.params.Username;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.response.Response;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.service.BossFightService;
import ru.tggc.capybaratelegrambot.service.CapybaraService;
import ru.tggc.capybaratelegrambot.service.CasinoService;

@BotHandler
@RequiredArgsConstructor
public class CommandTextHandler extends TextHandler {
    private final CapybaraService capybaraService;
    private final CasinoService casinoService;
    private final BossFightService bossFightService;
    private final InlineKeyboardCreator inlineKeyboardCreator;

    @MessageHandle("уволиться с работы")

    public Response dismissal(@Ctx CapybaraContext ctx) {
        capybaraService.dismissal(ctx);
        return sendSimpleMessage(ctx.chatId(), "ur capy has no work now");
    }

    @MessageHandle("казино")
    public Response startCasino(@Ctx CapybaraContext ctx) {
        casinoService.startCasino(ctx);
        return sendSimpleMessage(ctx.chatId(), "Введите ставку");
    }

    @MessageHandle("слоты")
    public Response startSlots(@Ctx CapybaraContext ctx) {
        casinoService.startSlots(ctx);
        return sendSimpleMessage(ctx.chatId(), "Введите ставку");
    }

    @MessageHandle("перевести дольки ${amount} ${username}")
    public Response transferMoney(@HandleParam("amount") Long amount,
                                  @HandleParam("username") String username,
                                  @MessageParam Message message,
                                  @Ctx CapybaraContext ctx) {
        String targetUsername = getTargetUsername(username, message);
        capybaraService.transferMoney(ctx, targetUsername, amount); //todo доработать

        return sendSimpleMessage(ctx.chatId(), "ok");
    }

    @MessageHandle("test join")
    public Response test(@Ctx CapybaraContext ctx, @Username String username) {
        bossFightService.joinFight(ctx, username);
        return sendSimpleMessage(ctx.chatId(), "Ты участвуешь теперь");
    }

    @MessageHandle("start fight")
    public Response startFight(@Ctx CapybaraContext ctx, @Username String username) {
        return sendSimpleMessage(ctx.chatId(), bossFightService.startFight(ctx.chatId()), inlineKeyboardCreator.fightKeyboard());
    }
}
