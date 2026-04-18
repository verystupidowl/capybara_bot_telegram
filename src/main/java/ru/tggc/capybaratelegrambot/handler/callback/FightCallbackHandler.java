package ru.tggc.capybaratelegrambot.handler.callback;

import com.pengrad.telegrambot.model.CallbackQuery;
import lombok.RequiredArgsConstructor;
import ru.tggc.capybaratelegrambot.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.annotation.handle.CallbackHandle;
import ru.tggc.capybaratelegrambot.annotation.params.CallbackParam;
import ru.tggc.capybaratelegrambot.annotation.params.Ctx;
import ru.tggc.capybaratelegrambot.annotation.params.HandleParam;
import ru.tggc.capybaratelegrambot.annotation.params.Username;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.dto.FightCapybaraDto;
import ru.tggc.capybaratelegrambot.domain.dto.UserDto;
import ru.tggc.capybaratelegrambot.domain.fight.enums.PlayerActionType;
import ru.tggc.capybaratelegrambot.domain.model.enums.fight.BuffType;
import ru.tggc.capybaratelegrambot.domain.response.Response;
import ru.tggc.capybaratelegrambot.keyboard.KeyboardFactory;
import ru.tggc.capybaratelegrambot.keyboard.KeyboardType;
import ru.tggc.capybaratelegrambot.service.BossFightService;
import ru.tggc.capybaratelegrambot.service.CapybaraService;
import ru.tggc.capybaratelegrambot.utils.Text;

@BotHandler
@RequiredArgsConstructor
public class FightCallbackHandler extends CallbackHandler {
    private final BossFightService bossFightService;
    private final CapybaraService capybaraService;
    private final KeyboardFactory keyboardFactory;

    @CallbackHandle("fight_action_${action}")
    public Response fightStep(@Ctx CapybaraContext ctx,
                              @Username String username,
                              @CallbackParam CallbackQuery query,
                              @HandleParam("action") PlayerActionType actionType) {
        return bossFightService.registerAction(query, new UserDto(ctx.userId(), username), actionType);
    }

    @CallbackHandle("fight_info")
    public Response fightInfo(@Ctx CapybaraContext ctx) {
        FightCapybaraDto fightInfo = capybaraService.getFightInfo(ctx);
        return editMessageCaption(
                ctx.chatId(),
                ctx.messageId(),
                Text.getFightInfo(fightInfo),
                keyboardFactory.getKeyboardInline(KeyboardType.FIGHT_INFO, fightInfo)
        );
    }

    @CallbackHandle("join_fight")
    public Response joinFight(@Ctx CapybaraContext ctx, @Username String username) {
        String response = bossFightService.joinFight(ctx, username);
        return editMessageCaption(ctx.chatId(), ctx.messageId(), response, keyboardFactory.getKeyboardInline(KeyboardType.LEAVE_FIGHT));
    }

    @CallbackHandle("leave_fight")
    public Response leaveFight(@Ctx CapybaraContext ctx) {
        bossFightService.leaveFight(ctx.chatId(), ctx.userId());
        return editMessageCaption(ctx.chatId(), ctx.messageId(), "Да уж", keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU));
    }

    @CallbackHandle("start_fight")
    public Response startFight(@Ctx CapybaraContext ctx) {
        return editMessageCaption(
                ctx.chatId(),
                ctx.messageId(),
                bossFightService.startFight(ctx.chatId()),
                keyboardFactory.getKeyboardInline(KeyboardType.FIGHT)
        );
    }

    @CallbackHandle("maybe_start_fight")
    public Response maybeStartFight(@Ctx CapybaraContext ctx) {
        return editMessageCaption(ctx.chatId(), ctx.messageId(), bossFightService.getUsers(ctx), keyboardFactory.getKeyboardInline(KeyboardType.MAYBE_START_FIGHT));
    }

    @CallbackHandle("list_of_buffs")
    public Response listOfBuffs(@Ctx CapybaraContext ctx) {
        return editMessageCaption(ctx.chatId(), ctx.messageId(), "Выбери тип", keyboardFactory.getKeyboardInline(KeyboardType.FIGHT_BUFF_TYPES));
    }

    @CallbackHandle("fight_buffs_${buffType}")
    public Response fightBuffs(@Ctx CapybaraContext ctx, @HandleParam("buffType") BuffType buffType) {
        String buffs = Text.getBuffs(buffType);
        return editMessageCaption(ctx.chatId(), ctx.messageId(), buffs, keyboardFactory.getKeyboardInline(KeyboardType.FIGHT_BUFFS, buffType));
    }

    @CallbackHandle("buy_buff_${buff}_${buffType}")
    public Response buyBuff(@Ctx CapybaraContext ctx,
                            @HandleParam("buff") String buff,
                            @HandleParam("buffType") BuffType buffType) {
        capybaraService.buyBuff(ctx, buff, buffType);
        return editMessageCaption(ctx.chatId(), ctx.messageId(), "u bought a buff", keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU));
    }
}
