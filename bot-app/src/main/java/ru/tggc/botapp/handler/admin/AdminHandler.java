package ru.tggc.botapp.handler.admin;

import ru.tggc.botapp.domain.dto.AdminStats;
import ru.tggc.botapp.exceptions.CapybaraException;
import ru.tggc.botapp.formatter.msgkey.CommonMsgKey;
import ru.tggc.botapp.keyboard.KeyboardType;
import ru.tggc.botapp.service.AdminService;
import ru.tggc.botapp.util.HistoryType;
import ru.tggc.telegrambotcore.annotation.handle.BotHandler;
import ru.tggc.telegrambotcore.annotation.handle.CallbackHandle;
import ru.tggc.telegrambotcore.annotation.handle.MessageHandle;
import ru.tggc.telegrambotcore.annotation.params.Ctx;
import ru.tggc.telegrambotcore.annotation.params.HandleParam;
import ru.tggc.telegrambotcore.annotation.params.Username;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.dto.UserRole;
import ru.tggc.telegrambotcore.formatter.FormatService;
import ru.tggc.telegrambotcore.keyboard.KeyboardFactory;
import ru.tggc.telegrambotcore.service.HistoryService;

import java.util.Locale;

@BotHandler
public record AdminHandler(AdminService adminService,
                           KeyboardFactory keyboardFactory,
                           HistoryService historyService,
                           FormatService formatService) {
    @CallbackHandle(
            value = "admin_menu",
            canPublic = false,
            canPrivate = true,
            requiredRoles = {UserRole.ADMIN, UserRole.SUPER_ADMIN}
    )
    public Response adminMenu(@Ctx UpdateContext ctx) {
        AdminStats stats = adminService.getStats();
        return ctx.send(
                stats.messageToSend(),
                keyboardFactory.getKeyboardInline(KeyboardType.ADMIN_MENU)
        );
    }

    @CallbackHandle(
            value = "broadcast",
            canPublic = false,
            canPrivate = true,
            requiredRoles = {UserRole.ADMIN, UserRole.SUPER_ADMIN}
    )
    public Response startBroadcast(@Ctx UpdateContext ctx) {
        historyService.setHistory(ctx, HistoryType.BROADCAST, prev -> {
            throw new CapybaraException(
                    formatService.get(CommonMsgKey.ALREADY_DOING, prev.state().getLabel()),
                    keyboardFactory.getKeyboardInline(KeyboardType.NOT_CHANGE)
            );
        });
        return ctx.send("Введите сообщение для рассылки!");
    }

    @MessageHandle(value = "Админка",
            canPrivate = true,
            canPublic = false,
            requiredRoles = {UserRole.ADMIN, UserRole.SUPER_ADMIN}
    )
    public Response openAdmin(@Ctx UpdateContext ctx) {
        AdminStats stats = adminService.getStats();
        return ctx.send(
                stats.messageToSend(),
                keyboardFactory.getKeyboardInline(KeyboardType.ADMIN_MENU)
        );
    }

    @MessageHandle(value = "block ${username} ${reason}",
            canPrivate = true,
            requiredRoles = {UserRole.ADMIN, UserRole.SUPER_ADMIN}
    )
    public Response block(@Ctx UpdateContext ctx,
                          @HandleParam("reason") String reason,
                          @HandleParam("username") String username,
                          @Username String reporterUsername) {
        username = username.toLowerCase(Locale.ROOT).replace("@", "");
        adminService.blockUser(username, reason, reporterUsername);
        return ctx.send("Пользователь " + username + " забанен по причине " + reason);
    }
}
