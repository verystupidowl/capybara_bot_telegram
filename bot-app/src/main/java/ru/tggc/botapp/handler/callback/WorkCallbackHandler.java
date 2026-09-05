package ru.tggc.botapp.handler.callback;

import ru.tggc.botapp.domain.model.enums.work.WorkType;
import ru.tggc.botapp.service.capybara.ServiceFacade;
import ru.tggc.telegrambotcore.annotation.handle.BotHandler;
import ru.tggc.telegrambotcore.annotation.handle.CallbackHandle;
import ru.tggc.telegrambotcore.annotation.params.Ctx;
import ru.tggc.telegrambotcore.annotation.params.HandleParam;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.UpdateContext;

@BotHandler
public record WorkCallbackHandler(ServiceFacade serviceFacade) {
    @CallbackHandle("take_from_work")
    public Response takeFromWork(@Ctx UpdateContext ctx) {
        return serviceFacade.takeFromWork(ctx);
    }

    @CallbackHandle("go_job")
    public Response goJob(@Ctx UpdateContext ctx) {
        return serviceFacade.goJob(ctx);
    }

    @CallbackHandle("set_job_${jobType}")
    public Response setJob(@Ctx UpdateContext ctx, @HandleParam("jobType") WorkType workType) {
        return serviceFacade.setJob(ctx, workType);
    }

    @CallbackHandle("get_works")
    public Response getWorkList(@Ctx UpdateContext ctx) {
        return serviceFacade.getWorks(ctx);
    }
}
