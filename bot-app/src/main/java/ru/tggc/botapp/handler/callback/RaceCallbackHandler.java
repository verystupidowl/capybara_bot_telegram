package ru.tggc.botapp.handler.callback;

import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.enums.ImprovementValue;
import ru.tggc.botapp.formatter.msgkey.ErrorMsgKey;
import ru.tggc.botapp.keyboard.KeyboardFactory;
import ru.tggc.botapp.keyboard.KeyboardKey;
import ru.tggc.botapp.service.CapybaraService;
import ru.tggc.botapp.service.RaceService;
import ru.tggc.botapp.util.Text;
import ru.tggc.telegrambotcore.annotation.handle.BotHandler;
import ru.tggc.telegrambotcore.annotation.handle.CallbackHandle;
import ru.tggc.telegrambotcore.annotation.params.Ctx;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.formatter.FormatService;

@BotHandler
public record RaceCallbackHandler(CapybaraService capybaraService,
                                  KeyboardFactory keyboardFactory,
                                  RaceService raceService,
                                  FormatService formatService) {
    @CallbackHandle("start_race")
    public Response startRace(@Ctx UpdateContext ctx) {
        raceService.startRace(ctx);
        return ctx.send(Text.START_RACE, keyboardFactory.getKeyboardInline(KeyboardKey.NOT_CHANGE));//todo
    }

    @CallbackHandle("improve_pills")
    public Response improvePills(@Ctx UpdateContext ctx) {
        capybaraService.setImprovement(ctx, ImprovementValue.ANTI_LOSE);
        return ctx.send(Text.ANTI_LOSE);
    }

    @CallbackHandle("improve_watermelon")
    public Response improveWatermelon(@Ctx UpdateContext ctx) {
        capybaraService.setImprovement(ctx, ImprovementValue.WATERMELON);
        return ctx.send(Text.WATERMELON);
    }

    @CallbackHandle("improve_boots")
    public Response improveBoots(@Ctx UpdateContext ctx) {
        capybaraService.setImprovement(ctx, ImprovementValue.BOOTS);
        return ctx.send(Text.BOOTS);
    }

    @CallbackHandle("buy_improve")
    public Response buyImprove(@Ctx UpdateContext ctx) {
        Capybara capybara = capybaraService.getRaceCapybara(ctx);
        if (capybara.getImprovement().getImprovementValue() == ImprovementValue.NONE) {
            return ctx.edit(Text.LIST_OF_IMPROVEMENTS, keyboardFactory.getKeyboardInline(KeyboardKey.IMPROVEMENTS));
        }
        return ctx.send(formatService.getMessage(ErrorMsgKey.CAPYBARA_ALREADY_HAS_IMPROVEMENT));
    }

    @CallbackHandle("do_massage")
    public Response doMassage(@Ctx UpdateContext ctx) {
        capybaraService.doMassage(ctx);
        return ctx.edit("Ты сделал своей капибаре массаж и восстановил ей всю выносливость!");
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
