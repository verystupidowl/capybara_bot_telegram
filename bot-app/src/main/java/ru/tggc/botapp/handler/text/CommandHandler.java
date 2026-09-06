package ru.tggc.botapp.handler.text;

import ru.tggc.botapp.formatter.common.CapybaraFormatter;
import ru.tggc.botapp.formatter.msgkey.CommonMsgKey;
import ru.tggc.botapp.service.CommonService;
import ru.tggc.botapp.service.capybara.ServiceFacade;
import ru.tggc.telegrambotcore.annotation.handle.BotHandler;
import ru.tggc.telegrambotcore.annotation.handle.CommandHandle;
import ru.tggc.telegrambotcore.annotation.params.Ctx;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.formatter.FormatService;
import ru.tggc.telegrambotcore.keyboard.KeyboardFactory;

@BotHandler
public record CommandHandler(ServiceFacade serviceFacade,
                             KeyboardFactory keyboardFactory,
                             CommonService commonService,
                             CapybaraFormatter capybaraFormatter,
                             FormatService formatService) {
    @CommandHandle(value = "start", canPrivate = true, canPublic = false)
    public Response start(@Ctx UpdateContext ctx) {
        return commonService.start(ctx);
    }

    @CommandHandle(value = "command_list", canPrivate = true)
    public Response sendCommandList(@Ctx UpdateContext ctx) {
        return ctx.send(formatService.get(CommonMsgKey.LIST_OF_COMMANDS));
    }

    @CommandHandle("my_capybara")
    public Response myCapybara(@Ctx UpdateContext ctx) {
        return serviceFacade.getMyCapybara(ctx);
    }

    @CommandHandle(value = "top_capybar", canPrivate = true)
    public Response top(@Ctx UpdateContext ctx) {
        return serviceFacade.getTopCapybaras(ctx);
    }

    @CommandHandle("take_capybara")
    public Response takeCapybara(@Ctx UpdateContext ctx) {
        return serviceFacade.saveCapybara(ctx);
    }

    @CommandHandle("bug_report")
    public Response bugReport(@Ctx UpdateContext ctx) {
        return commonService.startBugReport(ctx);
    }
}
