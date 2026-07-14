package ru.tggc.botapp.handler.callback;

import ru.tggc.botapp.domain.model.enums.WorkType;
import ru.tggc.botapp.keyboard.KeyboardFactory;
import ru.tggc.botapp.keyboard.KeyboardKey;
import ru.tggc.botapp.service.CapybaraService;
import ru.tggc.telegrambotcore.annotation.handle.BotHandler;
import ru.tggc.telegrambotcore.annotation.handle.CallbackHandle;
import ru.tggc.telegrambotcore.annotation.params.Ctx;
import ru.tggc.telegrambotcore.annotation.params.HandleParam;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.UpdateContext;

import java.util.List;

@BotHandler
public record WorkCallbackHandler(CapybaraService capybaraService,
                                  KeyboardFactory keyboardFactory) {
    @CallbackHandle("take_from_work")
    public Response takeFromWork(@Ctx UpdateContext ctx) {
        List<String> texts = capybaraService.takeFromWork(ctx);
        return ctx.send(texts);

    }

    @CallbackHandle("go_job")
    public Response goJob(@Ctx UpdateContext ctx) {
        capybaraService.goJob(ctx);
        return ctx.send("ur capy has gone to work");
    }

    @CallbackHandle("set_job_${jobType}")
    public Response setJob(@Ctx UpdateContext ctx,
                           @HandleParam("jobType") WorkType workType) {
        String photoUrl = capybaraService.setJob(ctx, workType);
        return ctx.edit(
                photoUrl,
                "Твоя капибара теперь " + workType.getLabel() + "! Поздравляю!"
        );
    }

    @CallbackHandle("get_job")
    public Response getJob(@Ctx UpdateContext ctx) {
        boolean hasWork = capybaraService.hasWork(ctx);
        if (!hasWork) {
            return ctx.edit("Выбери работу", keyboardFactory.getKeyboardInline(KeyboardKey.NEW_WORK));
        } else {
            return ctx.edit("Твоя капибара уже имеет работу");
        }
    }
}
