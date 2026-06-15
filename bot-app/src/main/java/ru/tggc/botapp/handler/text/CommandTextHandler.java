package ru.tggc.botapp.handler.text;

import com.pengrad.telegrambot.model.Message;
import lombok.RequiredArgsConstructor;
import ru.tggc.botapp.keyboard.KeyboardFactory;
import ru.tggc.botapp.keyboard.KeyboardKey;
import ru.tggc.botapp.service.BossFightService;
import ru.tggc.botapp.service.CapybaraService;
import ru.tggc.botapp.service.CasinoService;
import ru.tggc.botapp.service.impl.UserServiceImpl;
import ru.tggc.telegrambotframework.annotation.handle.BotHandler;
import ru.tggc.telegrambotframework.annotation.handle.MessageHandle;
import ru.tggc.telegrambotframework.annotation.params.Ctx;
import ru.tggc.telegrambotframework.annotation.params.HandleParam;
import ru.tggc.telegrambotframework.annotation.params.MessageParam;
import ru.tggc.telegrambotframework.annotation.params.Username;
import ru.tggc.telegrambotframework.dto.Response;
import ru.tggc.telegrambotframework.dto.UpdateContext;
import ru.tggc.telegrambotframework.dto.UserRole;

@BotHandler
@RequiredArgsConstructor
public class CommandTextHandler extends TextHandler {
    private final CapybaraService capybaraService;
    private final CasinoService casinoService;
    private final BossFightService bossFightService;
    private final KeyboardFactory keyboardFactory;
    private final UserServiceImpl userServiceImpl;

    @MessageHandle("уволиться с работы")
    public Response dismissal(@Ctx UpdateContext ctx) {
        capybaraService.dismissal(ctx);
        return sendSimpleMessage(ctx.chatId(), "ur capy has no work now");
    }

    @MessageHandle("казино")
    public Response startCasino(@Ctx UpdateContext ctx) {
        casinoService.startCasino(ctx);
        return sendSimpleMessage(ctx.chatId(), "Введите ставку");
    }

    @MessageHandle("слоты")
    public Response startSlots(@Ctx UpdateContext ctx) {
        casinoService.startSlots(ctx);
        return sendSimpleMessage(ctx.chatId(), "Введите ставку");
    }

    @MessageHandle("перевести дольки ${amount} ${username}")
    public Response transferMoney(@HandleParam("amount") Long amount,
                                  @HandleParam("username") String username,
                                  @MessageParam Message message,
                                  @Ctx UpdateContext ctx) {
        String targetUsername = getTargetUsername(username, message);
        capybaraService.transferMoney(ctx, targetUsername, amount); //todo доработать

        return sendSimpleMessage(ctx.chatId(), "ok");
    }

    @MessageHandle("test join")
    public Response test(@Ctx UpdateContext ctx, @Username String username) {
        bossFightService.joinFight(ctx, username);
        return sendSimpleMessage(ctx.chatId(), "Ты участвуешь теперь");
    }

    @MessageHandle("start fight")
    public Response startFight(@Ctx UpdateContext ctx, @Username String username) {
        return sendSimpleMessage(ctx.chatId(), bossFightService.startFight(ctx.chatId()), keyboardFactory.getKeyboardInline(KeyboardKey.FIGHT));
    }

    @MessageHandle(value = "block ${reason}", requiredRoles = {UserRole.ADMIN})
    public Response block(@MessageParam Message message,
                          @Ctx UpdateContext ctx,
                          @HandleParam("reason") String reason) {
        String targetUserName = getTargetUsername(null, message);
        userServiceImpl.blockUser(targetUserName, reason);
        return sendSimpleMessage(ctx.chatId(), "Пользователь " + targetUserName + " забанен по причине " + reason);
    }
}
