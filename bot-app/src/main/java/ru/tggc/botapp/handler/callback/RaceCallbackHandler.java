package ru.tggc.botapp.handler.callback;

import ru.tggc.botapp.domain.model.enums.ImprovementValue;
import ru.tggc.botapp.exceptions.CapybaraException;
import ru.tggc.botapp.formatter.msgkey.CommonMsgKey;
import ru.tggc.botapp.formatter.msgkey.RaceMsgKey;
import ru.tggc.botapp.keyboard.KeyboardType;
import ru.tggc.botapp.service.RaceService;
import ru.tggc.botapp.service.capybara.ServiceFacade;
import ru.tggc.botapp.util.HistoryType;
import ru.tggc.telegrambotcore.annotation.handle.BotHandler;
import ru.tggc.telegrambotcore.annotation.handle.CallbackHandle;
import ru.tggc.telegrambotcore.annotation.params.Ctx;
import ru.tggc.telegrambotcore.annotation.params.HandleParam;
import ru.tggc.telegrambotcore.dto.Access;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.formatter.FormatService;
import ru.tggc.telegrambotcore.keyboard.KeyboardFactory;

@BotHandler
public record RaceCallbackHandler(ServiceFacade serviceFacade,
                                  KeyboardFactory keyboardFactory,
                                  RaceService raceService,
                                  FormatService formatService) {
    @CallbackHandle("start_race")
    public Response startRace(@Ctx UpdateContext ctx) {
        raceService.startRace(ctx);
        return ctx.ask(
                formatService.get(RaceMsgKey.START_RACE),
                HistoryType.START_RACE,
                keyboardFactory.getKeyboardInline(KeyboardType.CANCEL),
                prev -> {
                    String message = formatService.get(CommonMsgKey.ALREADY_DOING, prev.state().getLabel());
                    throw new CapybaraException(message, keyboardFactory.getKeyboardInline(KeyboardType.RACE));
                }
        );
    }

    @CallbackHandle("improve_${improvement}")
    public Response buyImprovement(@Ctx UpdateContext ctx, @HandleParam("improvement") ImprovementValue improvement) {
        return serviceFacade.setImprovement(ctx, improvement);
    }

    @CallbackHandle("buy_improve")
    public Response getImprovements(@Ctx UpdateContext ctx) {
        return serviceFacade.getImprovements(ctx);
    }

    @CallbackHandle("do_massage")
    public Response doMassage(@Ctx UpdateContext ctx) {
        return serviceFacade.doMassage(ctx);
    }

    @CallbackHandle(value = "refuse_race", access = Access.ANYONE)
    public Response refuseRace(@Ctx UpdateContext ctx) {
        return raceService.refuseRace(ctx);
    }

    @CallbackHandle(value = "accept_race", access = Access.ANYONE)
    public Response acceptRace(@Ctx UpdateContext ctx) {
        return raceService.acceptRace(ctx);
    }
}
