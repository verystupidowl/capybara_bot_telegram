package ru.tggc.botapp.handler.callback;

import lombok.RequiredArgsConstructor;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.enums.ImprovementValue;
import ru.tggc.botapp.keyboard.KeyboardFactory;
import ru.tggc.botapp.keyboard.KeyboardKey;
import ru.tggc.botapp.service.CapybaraService;
import ru.tggc.botapp.service.RaceService;
import ru.tggc.botapp.util.Text;
import ru.tggc.telegrambotframework.annotation.handle.BotHandler;
import ru.tggc.telegrambotframework.annotation.handle.CallbackHandle;
import ru.tggc.telegrambotframework.annotation.params.Ctx;
import ru.tggc.telegrambotframework.dto.Response;
import ru.tggc.telegrambotframework.dto.UpdateContext;

@BotHandler
@RequiredArgsConstructor
public class RaceCallbackHandler extends CallbackHandler {
    private final CapybaraService capybaraService;
    private final KeyboardFactory keyboardFactory;
    private final RaceService raceService;

    @CallbackHandle("start_race")
    public Response startRace(@Ctx UpdateContext ctx) {
        raceService.startRace(ctx);
        return sendSimpleMessage(ctx.chatId(), Text.START_RACE);
    }

    @CallbackHandle("improve_pills")
    public Response improvePills(@Ctx UpdateContext ctx) {
        capybaraService.setImprovement(ctx, ImprovementValue.ANTI_LOSE);
        return sendSimpleMessage(ctx.chatId(), Text.ANTI_LOSE);
    }

    @CallbackHandle("improve_watermelon")
    public Response improveWatermelon(@Ctx UpdateContext ctx) {
        capybaraService.setImprovement(ctx, ImprovementValue.WATERMELON);
        return sendSimpleMessage(ctx.chatId(), Text.WATERMELON);
    }

    @CallbackHandle("improve_boots")
    public Response improveBoots(@Ctx UpdateContext ctx) {
        capybaraService.setImprovement(ctx, ImprovementValue.BOOTS);
        return sendSimpleMessage(ctx.chatId(), Text.BOOTS);
    }

    @CallbackHandle("buy_improve")
    public Response buyImprove(@Ctx UpdateContext ctx) {
        Capybara capybara = capybaraService.getRaceCapybara(ctx);
        if (capybara.getImprovement().getImprovementValue() == ImprovementValue.NONE) {
            return editMessageCaption(ctx.chatId(), ctx.messageId(), Text.LIST_OF_IMPROVEMENTS, keyboardFactory.getKeyboardInline(KeyboardKey.IMPROVEMENTS));
        }
        return sendSimpleMessage(ctx.chatId(), "У твоей капибары уже есть улучшение!");
    }

    @CallbackHandle("do_massage")
    public Response doMassage(@Ctx UpdateContext ctx) {
        capybaraService.doMassage(ctx);
        return editSimpleMessage(ctx.chatId(), ctx.messageId(), "Ты сделал своей капибаре массаж и восстановил ей всю выносливость!");
    }

    @CallbackHandle("refuse_race")
    public Response refuseRace(@Ctx UpdateContext ctx) {
        return raceService.refuseRace(ctx);
    }

    @CallbackHandle("accept_race")
    public Response acceptRace(@Ctx UpdateContext ctx) {
        return raceService.acceptRace(ctx);
    }
}
