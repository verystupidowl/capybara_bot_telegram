package ru.tggc.botapp.handler.callback;

import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.enums.ImprovementValue;
import ru.tggc.botapp.formatter.msgkey.CommonMsgKey;
import ru.tggc.botapp.formatter.msgkey.ErrorMsgKey;
import ru.tggc.botapp.formatter.msgkey.RaceMsgKey;
import ru.tggc.botapp.keyboard.KeyboardFactory;
import ru.tggc.botapp.keyboard.KeyboardKey;
import ru.tggc.botapp.service.CapybaraService;
import ru.tggc.botapp.service.RaceService;
import ru.tggc.telegrambotcore.annotation.handle.BotHandler;
import ru.tggc.telegrambotcore.annotation.handle.CallbackHandle;
import ru.tggc.telegrambotcore.annotation.params.Ctx;
import ru.tggc.telegrambotcore.annotation.params.HandleParam;
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
        return ctx.send(
                formatService.get(RaceMsgKey.START_RACE),
                keyboardFactory.getKeyboardInline(KeyboardKey.NOT_CHANGE)
        );
    }

    @CallbackHandle("improve_${improvement}")
    public Response improvePills(@Ctx UpdateContext ctx, @HandleParam("Improvement") ImprovementValue improvement) {
        capybaraService.setImprovement(ctx, improvement);
        return ctx.send(formatService.get(RaceMsgKey.getByImprovement(improvement)));
    }

    @CallbackHandle("buy_improve")
    public Response buyImprove(@Ctx UpdateContext ctx) {
        Capybara capybara = capybaraService.getRaceCapybara(ctx);
        if (capybara.getImprovement().getImprovementValue() == ImprovementValue.NONE) {
            String message = formatService.get(CommonMsgKey.LIST_OF_IMPROVEMENTS);
            return ctx.edit(message, keyboardFactory.getKeyboardInline(KeyboardKey.IMPROVEMENTS));
        }
        return ctx.send(formatService.get(ErrorMsgKey.CAPYBARA_ALREADY_HAS_IMPROVEMENT));
    }

    @CallbackHandle("do_massage")
    public Response doMassage(@Ctx UpdateContext ctx) {
        capybaraService.doMassage(ctx);
        return ctx.edit(formatService.get(RaceMsgKey.MASSAGE));
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
