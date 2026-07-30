package ru.tggc.botapp.handler.callback;

import ru.tggc.botapp.domain.model.enums.work.WorkType;
import ru.tggc.botapp.formatter.msgkey.WorkMsgKey;
import ru.tggc.botapp.keyboard.KeyboardType;
import ru.tggc.botapp.service.CapybaraService;
import ru.tggc.telegrambotcore.annotation.handle.BotHandler;
import ru.tggc.telegrambotcore.annotation.handle.CallbackHandle;
import ru.tggc.telegrambotcore.annotation.params.Ctx;
import ru.tggc.telegrambotcore.annotation.params.HandleParam;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.formatter.FormatService;
import ru.tggc.telegrambotcore.keyboard.KeyboardFactory;

@BotHandler
public record WorkCallbackHandler(CapybaraService capybaraService,
                                  KeyboardFactory keyboardFactory,
                                  FormatService formatService) {
    @CallbackHandle("take_from_work")
    public Response takeFromWork(@Ctx UpdateContext ctx) {
        return ctx.edit(capybaraService.takeFromWork(ctx), keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU));

    }

    @CallbackHandle("go_job")
    public Response goJob(@Ctx UpdateContext ctx) {
        return ctx.edit(capybaraService.goJob(ctx));
    }

    @CallbackHandle("set_job_${jobType}")
    public Response setJob(@Ctx UpdateContext ctx, @HandleParam("jobType") WorkType workType) {
        String photoUrl = capybaraService.setJob(ctx, workType);
        return ctx.edit(
                photoUrl,
                formatService.get(WorkMsgKey.NEW_WORK, workType.getLabel()),
                keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU)
        );
    }

    @CallbackHandle("get_job")
    public Response getJob(@Ctx UpdateContext ctx) {
        boolean hasWork = capybaraService.hasWork(ctx);
        if (!hasWork) {
            return ctx.edit("Выбери работу", keyboardFactory.getKeyboardInline(KeyboardType.NEW_WORK));
        } else {
            return ctx.edit("Твоя капибара уже имеет работу");
        }
    }
}
