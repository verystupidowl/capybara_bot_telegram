package ru.tggc.capybaratelegrambot.handler.callback;

import lombok.RequiredArgsConstructor;
import ru.tggc.capybaratelegrambot.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.annotation.handle.CallbackHandle;
import ru.tggc.capybaratelegrambot.annotation.params.Ctx;
import ru.tggc.capybaratelegrambot.annotation.params.HandleParam;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.dto.response.Response;
import ru.tggc.capybaratelegrambot.domain.model.enums.WorkType;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.service.CapybaraService;

@BotHandler
@RequiredArgsConstructor
public class WorkCallbackHandler extends CallbackHandler {
    private final CapybaraService capybaraService;
    private final InlineKeyboardCreator inlineCreator;


    @CallbackHandle("take_from_work")
    public Response takeFromWork(@Ctx CapybaraContext ctx) {
        return sendSimpleMessages(ctx.chatId(), capybaraService.takeFromWork(ctx));

    }

    @CallbackHandle("go_job")
    public Response goJob(@Ctx CapybaraContext ctx) {
        capybaraService.goJob(ctx);
        return sendSimpleMessage(ctx.chatId(), "ur capy has gone to work");
    }

    @CallbackHandle("set_job_${jobType}")
    public Response setJob(@Ctx CapybaraContext ctx,
                           @HandleParam("jobType") WorkType workType) {
        String photoUrl = capybaraService.setJob(ctx, workType);
        return editPhoto(
                ctx.chatId(),
                ctx.messageId(),
                photoUrl,
                "Твоя капибара теперь " + workType.getLabel() + "! Поздравляю!"
        );
    }

    @CallbackHandle("get_job")
    public Response getJob(@Ctx CapybaraContext ctx) {
        boolean hasWork = capybaraService.hasWork(ctx);
        if (!hasWork) {
            return editMessageCaption(ctx.chatId(), ctx.messageId(), "Выбери работу", inlineCreator.newJob());
        } else {
            return editMessageCaption(ctx.chatId(), ctx.messageId(), "Твоя капибара уже имеет работу", null);
        }
    }
}
