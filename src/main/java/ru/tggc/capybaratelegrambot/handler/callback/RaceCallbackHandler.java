package ru.tggc.capybaratelegrambot.handler.callback;

import lombok.RequiredArgsConstructor;
import ru.tggc.capybaratelegrambot.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.annotation.handle.CallbackHandle;
import ru.tggc.capybaratelegrambot.annotation.params.Ctx;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.response.Response;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.enums.ImprovementValue;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.service.CapybaraService;
import ru.tggc.capybaratelegrambot.service.RaceService;
import ru.tggc.capybaratelegrambot.utils.Text;

@BotHandler
@RequiredArgsConstructor
public class RaceCallbackHandler extends CallbackHandler {
    private final CapybaraService capybaraService;
    private final InlineKeyboardCreator inlineCreator;
    private final RaceService raceService;

    @CallbackHandle("start_race")
    public Response startRace(@Ctx CapybaraContext ctx) {
        raceService.startRace(ctx);
        return sendSimpleMessage(ctx.chatId(), Text.START_RACE);
    }

    @CallbackHandle("improve_pills")
    public Response improvePills(@Ctx CapybaraContext ctx) {
        capybaraService.setImprovement(ctx, ImprovementValue.ANTI_LOSE);
        return sendSimpleMessage(ctx.chatId(), Text.ANTI_LOSE);
    }

    @CallbackHandle("improve_watermelon")
    public Response improveWatermelon(@Ctx CapybaraContext ctx) {
        capybaraService.setImprovement(ctx, ImprovementValue.WATERMELON);
        return sendSimpleMessage(ctx.chatId(), Text.WATERMELON);
    }

    @CallbackHandle("improve_boots")
    public Response improveBoots(@Ctx CapybaraContext ctx) {
        capybaraService.setImprovement(ctx, ImprovementValue.BOOTS);
        return sendSimpleMessage(ctx.chatId(), Text.BOOTS);
    }

    @CallbackHandle("buy_improve")
    public Response buyImprove(@Ctx CapybaraContext ctx) {
        Capybara capybara = capybaraService.getRaceCapybara(ctx);
        if (capybara.getImprovement().getImprovementValue() == ImprovementValue.NONE) {
            return editMessageCaption(ctx.chatId(), ctx.messageId(), Text.LIST_OF_IMPROVEMENTS, inlineCreator.improvements());
        }
        return sendSimpleMessage(ctx.chatId(), "У твоей капибары уже есть улучшение!");
    }

    @CallbackHandle("do_massage")
    public Response doMassage(@Ctx CapybaraContext ctx) {
        capybaraService.doMassage(ctx);
        return editSimpleMessage(ctx.chatId(), ctx.messageId(), "Ты сделал своей капибаре массаж и восстановил ей всю выносливость!");
    }

    @CallbackHandle("refuse_race")
    public Response refuseRace(@Ctx CapybaraContext ctx) {
        return raceService.refuseRace(ctx);
    }

    @CallbackHandle("accept_race")
    public Response acceptRace(@Ctx CapybaraContext ctx) {
        return raceService.acceptRace(ctx);
    }
}
