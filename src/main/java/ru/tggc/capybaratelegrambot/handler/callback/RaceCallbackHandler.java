package ru.tggc.capybaratelegrambot.handler.callback;

import com.pengrad.telegrambot.model.CallbackQuery;
import ru.tggc.capybaratelegrambot.aop.CallbackRegistry;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.CallbackHandle;
import ru.tggc.capybaratelegrambot.aop.annotation.params.CallbackParam;
import ru.tggc.capybaratelegrambot.aop.annotation.params.Ctx;
import ru.tggc.capybaratelegrambot.aop.annotation.params.MessageId;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.enums.ImprovementValue;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.sender.Sender;
import ru.tggc.capybaratelegrambot.service.impl.CapybaraService;

@BotHandler
public class RaceCallbackHandler extends CallbackHandler {
    private final CapybaraService capybaraService;
    private final InlineKeyboardCreator inlineCreator;

    public RaceCallbackHandler(CallbackRegistry callbackRegistry,
                               Sender sender,
                               CapybaraService capybaraService,
                               InlineKeyboardCreator inlineCreator) {
        super(sender, callbackRegistry);
        this.capybaraService = capybaraService;
        this.inlineCreator = inlineCreator;
    }

    @CallbackHandle("improve_pills")
    public void improvePills(@MessageId int messageId,
                             @Ctx CapybaraContext ctx) {
        capybaraService.setImprovement(ctx, ImprovementValue.ANTI_LOSE);
        sendSimpleMessage(ctx.chatId(), messageId, "ur capy taken a pill", null);
    }

    @CallbackHandle("improve_watermelon")
    public void improveWatermelon(@MessageId int messageId,
                                  @Ctx CapybaraContext ctx) {
        capybaraService.setImprovement(ctx, ImprovementValue.WATERMELON);
        sendSimpleMessage(ctx.chatId(), messageId, "ur capy eaten a watermellon", null);
    }

    @CallbackHandle("improve_boots")
    private void improveBoots(@MessageId int messageId,
                              @Ctx CapybaraContext ctx) {
        capybaraService.setImprovement(ctx, ImprovementValue.BOOTS);
        sendSimpleMessage(ctx.chatId(), messageId, "ur capy wears boots", null);
    }

    @CallbackHandle("buy_improve")
    private void buyImprove(@MessageId int messageId,
                            @Ctx CapybaraContext ctx) {
        Capybara capybara = capybaraService.getCapybaraByContext(ctx);
        if (capybara.getImprovement().getImprovement() == ImprovementValue.NONE) {
            sendSimpleMessage(ctx.chatId(), messageId, "choose one", inlineCreator.improvements());
        }
    }

    @CallbackHandle("do_massage")
    private void doMassage(@MessageId int messageId,
                           @Ctx CapybaraContext ctx) {
        capybaraService.doMassage(ctx);
        sendSimpleMessage(ctx.chatId(), messageId, "u did a massge", null);
    }

    @CallbackHandle("refuse_race")
    private void refuseRace(@MessageId int messageId,
                            @Ctx CapybaraContext ctx) {
        capybaraService.refuseRace(ctx);
        sendSimpleMessage(ctx.chatId(), messageId, "u refused a race", null);
    }

    @CallbackHandle("accept_race")
    private void acceptRace(@CallbackParam CallbackQuery query,
                            @Ctx CapybaraContext ctx) {
        capybaraService.acceptRace(ctx).accept(this, query);
    }
}
