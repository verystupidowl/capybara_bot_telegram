package ru.tggc.botapp.handler.admin;

import ru.tggc.botapp.service.AdminService;
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

import java.util.Locale;

@BotHandler
public record AdminHandler(AdminService adminService,
                           FormatService formatService) {
    @CallbackHandle(
            value = "admin_menu",
            canPublic = false,
            canPrivate = true,
            requiredRoles = {UserRole.ADMIN, UserRole.SUPER_ADMIN}
    )
    public Response adminMenu(@Ctx UpdateContext ctx) {
        return adminService.getStats(ctx);
    }

    @CallbackHandle(
            value = "broadcast",
            canPublic = false,
            canPrivate = true,
            requiredRoles = {UserRole.ADMIN, UserRole.SUPER_ADMIN}
    )
    public Response broadcast(@Ctx UpdateContext ctx) {
        return adminService.broadcast(ctx);

    }

    @MessageHandle(value = "Админка",
            canPrivate = true,
            canPublic = false,
            requiredRoles = {UserRole.ADMIN, UserRole.SUPER_ADMIN}
    )
    public Response openAdmin(@Ctx UpdateContext ctx) {
        return adminService.getStats(ctx);
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
        return adminService.blockUser(username, reason, reporterUsername, ctx);
    }
}
