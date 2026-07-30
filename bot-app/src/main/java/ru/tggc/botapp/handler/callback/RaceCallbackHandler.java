package ru.tggc.botapp.handler.callback;

import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.enums.ImprovementValue;
import ru.tggc.botapp.exceptions.CapybaraException;
import ru.tggc.botapp.formatter.msgkey.CommonMsgKey;
import ru.tggc.botapp.formatter.msgkey.ErrorMsgKey;
import ru.tggc.botapp.formatter.msgkey.RaceMsgKey;
import ru.tggc.botapp.keyboard.KeyboardType;
import ru.tggc.botapp.service.CapybaraService;
import ru.tggc.botapp.service.RaceService;
import ru.tggc.botapp.util.HistoryType;
import ru.tggc.telegrambotcore.annotation.handle.BotHandler;
import ru.tggc.telegrambotcore.annotation.handle.CallbackHandle;
import ru.tggc.telegrambotcore.annotation.params.Ctx;
import ru.tggc.telegrambotcore.annotation.params.HandleParam;
import ru.tggc.telegrambotcore.dto.Access;
import ru.tggc.telegrambotcore.dto.PhotoDto;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.formatter.FormatService;
import ru.tggc.telegrambotcore.keyboard.KeyboardFactory;
import ru.tggc.telegrambotcore.service.HistoryService;

@BotHandler
public record RaceCallbackHandler(CapybaraService capybaraService,
                                  KeyboardFactory keyboardFactory,
                                  RaceService raceService,
                                  FormatService formatService,
                                  HistoryService historyService) {
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
    public Response improvePills(@Ctx UpdateContext ctx, @HandleParam("improvement") ImprovementValue improvement) {
        PhotoDto photoDto = capybaraService.setImprovement(ctx, improvement);
        return ctx.edit(photoDto);
    }

    @CallbackHandle("buy_improve")
    public Response buyImprove(@Ctx UpdateContext ctx) {
        Capybara capybara = capybaraService.getRaceCapybara(ctx);
        if (capybara.getImprovement().getImprovementValue() == ImprovementValue.NONE) {
            String message = formatService.get(CommonMsgKey.LIST_OF_IMPROVEMENTS);
            return ctx.edit(message, keyboardFactory.getKeyboardInline(KeyboardType.IMPROVEMENTS));
        }
        return ctx.send(formatService.get(ErrorMsgKey.CAPYBARA_ALREADY_HAS_IMPROVEMENT));
    }

    @CallbackHandle("do_massage")
    public Response doMassage(@Ctx UpdateContext ctx) {
        capybaraService.doMassage(ctx);
        return ctx.edit(formatService.get(RaceMsgKey.MASSAGE));
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
