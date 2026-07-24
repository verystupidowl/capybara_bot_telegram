package ru.tggc.botapp.handler.callback;

import com.pengrad.telegrambot.model.CallbackQuery;
import ru.tggc.botapp.domain.dto.FightCapybaraDto;
import ru.tggc.botapp.domain.model.enums.fight.BuffType;
import ru.tggc.botapp.fight.enums.PlayerActionType;
import ru.tggc.botapp.formatter.fight.FightFormatService;
import ru.tggc.botapp.keyboard.KeyboardType;
import ru.tggc.botapp.service.CapybaraService;
import ru.tggc.botapp.service.bossfight.BossFightService;
import ru.tggc.telegrambotcore.annotation.handle.BotHandler;
import ru.tggc.telegrambotcore.annotation.handle.CallbackHandle;
import ru.tggc.telegrambotcore.annotation.params.CallbackParam;
import ru.tggc.telegrambotcore.annotation.params.Ctx;
import ru.tggc.telegrambotcore.annotation.params.HandleParam;
import ru.tggc.telegrambotcore.annotation.params.Username;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.dto.UserDto;
import ru.tggc.telegrambotcore.keyboard.KeyboardFactory;

@BotHandler
public record FightCallbackHandler(BossFightService bossFightService,
                                   CapybaraService capybaraService,
                                   KeyboardFactory keyboardFactory,
                                   FightFormatService fightFormatService) {
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
        return ctx.edit(
                fightFormatService.getFightInfo(fightInfo),
                keyboardFactory.getKeyboardInline(KeyboardType.FIGHT_INFO, fightInfo)
        );
    }

    @CallbackHandle("join_fight")
    public Response joinFight(@Ctx UpdateContext ctx, @Username String username) {
        String response = bossFightService.joinFight(ctx, username);
        return ctx.edit(response, keyboardFactory.getKeyboardInline(KeyboardType.LEAVE_FIGHT));
    }

    @CallbackHandle("leave_fight")
    public Response leaveFight(@Ctx UpdateContext ctx) {
        bossFightService.leaveFight(ctx.chatId(), ctx.userId());
        return ctx.edit("Да уж", keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU));
    }

    @CallbackHandle("start_fight")
    public Response startFight(@Ctx UpdateContext ctx) {
        return ctx.edit(
                bossFightService.startFight(ctx.chatId()),
                keyboardFactory.getKeyboardInline(KeyboardType.FIGHT)
        );
    }

    @CallbackHandle("maybe_start_fight")
    public Response maybeStartFight(@Ctx UpdateContext ctx) {
        String message = bossFightService.getUsers(ctx);
        return ctx.edit(message, keyboardFactory.getKeyboardInline(KeyboardType.MAYBE_START_FIGHT));
    }

    @CallbackHandle("list_of_buffs")
    public Response listOfBuffs(@Ctx UpdateContext ctx) {
        return ctx.edit("Выбери тип", keyboardFactory.getKeyboardInline(KeyboardType.FIGHT_BUFF_TYPES));
    }

    @CallbackHandle("fight_buffs_${buffType}")
    public Response fightBuffs(@Ctx UpdateContext ctx, @HandleParam("buffType") BuffType buffType) {
        String buffs = fightFormatService.getBuffs(buffType);
        return ctx.edit(buffs, keyboardFactory.getKeyboardInline(KeyboardType.FIGHT_BUFFS, buffType));
    }

    @CallbackHandle("buy_buff_${buff}_${buffType}")
    public Response buyBuff(@Ctx UpdateContext ctx,
                            @HandleParam("buff") String buff,
                            @HandleParam("buffType") BuffType buffType) {
        capybaraService.buyBuff(ctx, buff, buffType);
        return ctx.edit("u bought a buff", keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU));
    }
}
