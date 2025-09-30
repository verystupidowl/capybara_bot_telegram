package ru.tggc.capybaratelegrambot.handler.callback;

import com.pengrad.telegrambot.model.CallbackQuery;
import lombok.RequiredArgsConstructor;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.CallbackHandle;
import ru.tggc.capybaratelegrambot.aop.annotation.params.CallbackParam;
import ru.tggc.capybaratelegrambot.aop.annotation.params.Ctx;
import ru.tggc.capybaratelegrambot.aop.annotation.params.MessageId;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.dto.response.Response;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.enums.ImprovementValue;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.service.CapybaraService;
import ru.tggc.capybaratelegrambot.service.RaceService;

@BotHandler
@RequiredArgsConstructor
public class RaceCallbackHandler extends CallbackHandler {
    private final CapybaraService capybaraService;
    private final InlineKeyboardCreator inlineCreator;
    private final RaceService raceService;

    @CallbackHandle("start_race")
    public Response startRace(@Ctx CapybaraContext ctx) {
        raceService.startRace(ctx);
        return sendSimpleMessage(ctx.chatId(), "Напиши ник пользователя, чью капибару ты хочешь вызвать на забег через @");
    }

    @CallbackHandle("improve_pills")
    public Response improvePills(@MessageId int messageId,
                                 @Ctx CapybaraContext ctx) {
        capybaraService.setImprovement(ctx, ImprovementValue.ANTI_LOSE);
        return editSimpleMessage(ctx.chatId(), messageId, "ur capy taken a pill");
    }

    @CallbackHandle("improve_watermelon")
    public Response improveWatermelon(@MessageId int messageId,
                                      @Ctx CapybaraContext ctx) {
        capybaraService.setImprovement(ctx, ImprovementValue.WATERMELON);
        return editSimpleMessage(ctx.chatId(), messageId, "ur capy eaten a watermelon");
    }

    @CallbackHandle("improve_boots")
    public Response improveBoots(@MessageId int messageId,
                                 @Ctx CapybaraContext ctx) {
        capybaraService.setImprovement(ctx, ImprovementValue.BOOTS);
        return editSimpleMessage(ctx.chatId(), messageId, "ur capy wears boots");
    }

    @CallbackHandle("buy_improve")
    public Response buyImprove(@MessageId int messageId,
                               @Ctx CapybaraContext ctx) {
        Capybara capybara = capybaraService.getCapybaraByContext(ctx);
        if (capybara.getImprovement().getImprovementValue() == ImprovementValue.NONE) {
            return editSimpleMessage(ctx.chatId(), messageId, "choose one", inlineCreator.improvements());
        }
        return editSimpleMessage(ctx.chatId(), messageId, "u already have an improvement");
    }

    @CallbackHandle("do_massage")
    public Response doMassage(@MessageId int messageId,
                              @Ctx CapybaraContext ctx) {
        capybaraService.doMassage(ctx);
        return editSimpleMessage(ctx.chatId(), messageId, "u did a massage");
    }

    @CallbackHandle("refuse_race")
    public Response refuseRace(@MessageId int messageId,
                               @Ctx CapybaraContext ctx) {
        return raceService.refuseRace(ctx);
    }

    @CallbackHandle("accept_race")
    public Response acceptRace(@CallbackParam CallbackQuery query,
                               @Ctx CapybaraContext ctx) {
        return raceService.acceptRace(ctx);
    }
}
