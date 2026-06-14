package ru.tggc.botapp.handler.callback;

import lombok.RequiredArgsConstructor;
import ru.tggc.botapp.domain.model.enums.WorkType;
import ru.tggc.botapp.keyboard.KeyboardFactory;
import ru.tggc.botapp.keyboard.KeyboardKey;
import ru.tggc.botapp.service.CapybaraService;
import ru.tggc.telegrambotframework.annotation.handle.BotHandler;
import ru.tggc.telegrambotframework.annotation.handle.CallbackHandle;
import ru.tggc.telegrambotframework.annotation.params.Ctx;
import ru.tggc.telegrambotframework.annotation.params.HandleParam;
import ru.tggc.telegrambotframework.dto.Response;
import ru.tggc.telegrambotframework.dto.UpdateContext;

@BotHandler
@RequiredArgsConstructor
public class WorkCallbackHandler extends CallbackHandler {
    private final CapybaraService capybaraService;
    private final KeyboardFactory keyboardFactory;

    @CallbackHandle("take_from_work")
    public Response takeFromWork(@Ctx UpdateContext ctx) {
        return sendSimpleMessages(ctx.chatId(), capybaraService.takeFromWork(ctx));

    }

    @CallbackHandle("go_job")
    public Response goJob(@Ctx UpdateContext ctx) {
        capybaraService.goJob(ctx);
        return sendSimpleMessage(ctx.chatId(), "ur capy has gone to work");
    }

    @CallbackHandle("set_job_${jobType}")
    public Response setJob(@Ctx UpdateContext ctx,
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
    public Response getJob(@Ctx UpdateContext ctx) {
        boolean hasWork = capybaraService.hasWork(ctx);
        if (!hasWork) {
            return editMessageCaption(ctx.chatId(), ctx.messageId(), "Выбери работу", keyboardFactory.getKeyboardInline(KeyboardKey.NEW_WORK));
        } else {
            return editMessageCaption(ctx.chatId(), ctx.messageId(), "Твоя капибара уже имеет работу", null);
        }
    }
}
