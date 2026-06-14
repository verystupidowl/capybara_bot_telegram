package ru.tggc.botapp.handler.callback;

import com.pengrad.telegrambot.model.CallbackQuery;
import lombok.RequiredArgsConstructor;
import ru.tggc.botapp.domain.dto.FightCapybaraDto;
import ru.tggc.botapp.domain.dto.fight.enums.PlayerActionType;
import ru.tggc.botapp.domain.model.enums.fight.BuffType;
import ru.tggc.botapp.keyboard.KeyboardFactory;
import ru.tggc.botapp.keyboard.KeyboardKey;
import ru.tggc.botapp.service.BossFightService;
import ru.tggc.botapp.service.CapybaraService;
import ru.tggc.botapp.util.Text;
import ru.tggc.telegrambotframework.annotation.handle.BotHandler;
import ru.tggc.telegrambotframework.annotation.handle.CallbackHandle;
import ru.tggc.telegrambotframework.annotation.params.CallbackParam;
import ru.tggc.telegrambotframework.annotation.params.Ctx;
import ru.tggc.telegrambotframework.annotation.params.HandleParam;
import ru.tggc.telegrambotframework.annotation.params.Username;
import ru.tggc.telegrambotframework.dto.Response;
import ru.tggc.telegrambotframework.dto.UpdateContext;
import ru.tggc.telegrambotframework.dto.UserDto;

@BotHandler
@RequiredArgsConstructor
public class FightCallbackHandler extends CallbackHandler {
    private final BossFightService bossFightService;
    private final CapybaraService capybaraService;
    private final KeyboardFactory keyboardFactory;

    @CallbackHandle("fight_action_${action}")
    public Response fightStep(@Ctx UpdateContext ctx,
                              @Username String username,
                              @CallbackParam CallbackQuery query,
                              @HandleParam("action") PlayerActionType actionType) {
        return bossFightService.registerAction(query, new UserDto(ctx.userId(), username), actionType);
    }

    @CallbackHandle("fight_info")
    public Response fightInfo(@Ctx UpdateContext ctx) {
        FightCapybaraDto fightInfo = capybaraService.getFightInfo(ctx);
        return editMessageCaption(
                ctx.chatId(),
                ctx.messageId(),
                Text.getFightInfo(fightInfo),
                keyboardFactory.getKeyboardInline(KeyboardKey.FIGHT_INFO, fightInfo)
        );
    }

    @CallbackHandle("join_fight")
    public Response joinFight(@Ctx UpdateContext ctx, @Username String username) {
        String response = bossFightService.joinFight(ctx, username);
        return editMessageCaption(ctx.chatId(), ctx.messageId(), response, keyboardFactory.getKeyboardInline(KeyboardKey.LEAVE_FIGHT));
    }

    @CallbackHandle("leave_fight")
    public Response leaveFight(@Ctx UpdateContext ctx) {
        bossFightService.leaveFight(ctx.chatId(), ctx.userId());
        return editMessageCaption(ctx.chatId(), ctx.messageId(), "Да уж", keyboardFactory.getKeyboardInline(KeyboardKey.TO_MAIN_MENU));
    }

    @CallbackHandle("start_fight")
    public Response startFight(@Ctx UpdateContext ctx) {
        return editMessageCaption(
                ctx.chatId(),
                ctx.messageId(),
                bossFightService.startFight(ctx.chatId()),
                keyboardFactory.getKeyboardInline(KeyboardKey.FIGHT)
        );
    }

    @CallbackHandle("maybe_start_fight")
    public Response maybeStartFight(@Ctx UpdateContext ctx) {
        return editMessageCaption(ctx.chatId(), ctx.messageId(), bossFightService.getUsers(ctx), keyboardFactory.getKeyboardInline(KeyboardKey.MAYBE_START_FIGHT));
    }

    @CallbackHandle("list_of_buffs")
    public Response listOfBuffs(@Ctx UpdateContext ctx) {
        return editMessageCaption(ctx.chatId(), ctx.messageId(), "Выбери тип", keyboardFactory.getKeyboardInline(KeyboardKey.FIGHT_BUFF_TYPES));
    }

    @CallbackHandle("fight_buffs_${buffType}")
    public Response fightBuffs(@Ctx UpdateContext ctx, @HandleParam("buffType") BuffType buffType) {
        String buffs = Text.getBuffs(buffType);
        return editMessageCaption(ctx.chatId(), ctx.messageId(), buffs, keyboardFactory.getKeyboardInline(KeyboardKey.FIGHT_BUFFS, buffType));
    }

    @CallbackHandle("buy_buff_${buff}_${buffType}")
    public Response buyBuff(@Ctx UpdateContext ctx,
                            @HandleParam("buff") String buff,
                            @HandleParam("buffType") BuffType buffType) {
        capybaraService.buyBuff(ctx, buff, buffType);
        return editMessageCaption(ctx.chatId(), ctx.messageId(), "u bought a buff", keyboardFactory.getKeyboardInline(KeyboardKey.TO_MAIN_MENU));
    }
}
