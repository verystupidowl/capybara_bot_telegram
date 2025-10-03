package ru.tggc.capybaratelegrambot.handler.callback;

import lombok.RequiredArgsConstructor;
import ru.tggc.capybaratelegrambot.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.annotation.handle.CallbackHandle;
import ru.tggc.capybaratelegrambot.annotation.params.Ctx;
import ru.tggc.capybaratelegrambot.annotation.params.HandleParam;
import ru.tggc.capybaratelegrambot.annotation.params.Username;
import ru.tggc.capybaratelegrambot.domain.dto.BossFightState;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.dto.FightCapybaraDto;
import ru.tggc.capybaratelegrambot.domain.dto.UserDto;
import ru.tggc.capybaratelegrambot.domain.model.enums.fight.BuffType;
import ru.tggc.capybaratelegrambot.domain.response.Response;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.service.BossFightService;
import ru.tggc.capybaratelegrambot.service.CapybaraService;
import ru.tggc.capybaratelegrambot.utils.Text;

@BotHandler
@RequiredArgsConstructor
public class FightCallbackHandler extends CallbackHandler {
    private final BossFightService bossFightService;
    private final CapybaraService capybaraService;
    private final InlineKeyboardCreator inlineKeyboardCreator;

    @CallbackHandle("fight_action_${action}")
    public Response fightStep(@Ctx CapybaraContext ctx,
                              @Username String username,
                              @HandleParam("action") BossFightState.ActionType actionType) {
        return bossFightService.registerAction(ctx.chatId(), new UserDto(ctx.userId(), username), actionType);
    }

    @CallbackHandle("fight_info")
    public Response fightInfo(@Ctx CapybaraContext ctx) {
        FightCapybaraDto fightInfo = capybaraService.getFightInfo(ctx);
        return editMessageCaption(
                ctx.chatId(),
                ctx.messageId(),
                Text.getFightInfo(fightInfo),
                inlineKeyboardCreator.fightInfoKeyboard(fightInfo, ctx.chatId())
        );
    }

    @CallbackHandle("join_fight")
    public Response joinFight(@Ctx CapybaraContext ctx, @Username String username) {
        bossFightService.joinFight(ctx, username);
        return editMessageCaption(ctx.chatId(), ctx.messageId(), "Ты участвуешь теперь", inlineKeyboardCreator.leaveFight());
    }

    @CallbackHandle("leave_fight")
    public Response leaveFight(@Ctx CapybaraContext ctx) {
        bossFightService.leaveFight(ctx.chatId(), ctx.userId());
        return editMessageCaption(ctx.chatId(), ctx.messageId(), "Да уж", inlineKeyboardCreator.toMainMenu());
    }

    @CallbackHandle("start_fight")
    public Response startFight(@Ctx CapybaraContext ctx) {
        return sendSimpleMessage(ctx.chatId(), bossFightService.startFight(ctx.chatId()), inlineKeyboardCreator.fightKeyboard());
    }

    @CallbackHandle("maybe_start_fight")
    public Response maybeStartFight(@Ctx CapybaraContext ctx) {
        return editMessageCaption(ctx.chatId(), ctx.messageId(), bossFightService.getUsers(ctx), inlineKeyboardCreator.maybeStartFight());
    }

    @CallbackHandle("list_of_buffs")
    public Response listOfBuffs(@Ctx CapybaraContext ctx) {
        return editMessageCaption(ctx.chatId(), ctx.messageId(), "Выбери тип", inlineKeyboardCreator.fightBuffTypes());
    }

    @CallbackHandle("fight_buffs_${buffType}")
    public Response fightBuffs(@Ctx CapybaraContext ctx, @HandleParam("buffType") BuffType buffType) {
        String buffs = Text.getBuffs(buffType);
        return editMessageCaption(ctx.chatId(), ctx.messageId(), buffs, inlineKeyboardCreator.fightBuffs(buffType));
    }

    @CallbackHandle("buy_buff_${buff}_${buffType}")
    public Response buyBuff(@Ctx CapybaraContext ctx,
                            @HandleParam("buff") String buff,
                            @HandleParam("buffType") BuffType buffType) {
        capybaraService.buyBuff(ctx, buff, buffType);
        return editMessageCaption(ctx.chatId(), ctx.messageId(), "u bought a buff", inlineKeyboardCreator.toMainMenu());
    }
}
