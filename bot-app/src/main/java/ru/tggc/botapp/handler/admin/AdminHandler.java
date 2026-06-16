package ru.tggc.botapp.handler.admin;

import com.pengrad.telegrambot.model.Message;
import lombok.RequiredArgsConstructor;
import ru.tggc.botapp.domain.dto.AdminStats;
import ru.tggc.botapp.handler.callback.CallbackHandler;
import ru.tggc.botapp.keyboard.KeyboardFactory;
import ru.tggc.botapp.keyboard.KeyboardKey;
import ru.tggc.botapp.service.AdminService;
import ru.tggc.botapp.service.impl.HistoryServiceImpl;
import ru.tggc.botapp.service.impl.UserServiceImpl;
import ru.tggc.botapp.util.HistoryType;
import ru.tggc.botapp.util.TextBuilder;
import ru.tggc.telegrambotframework.annotation.handle.BotHandler;
import ru.tggc.telegrambotframework.annotation.handle.CallbackHandle;
import ru.tggc.telegrambotframework.annotation.handle.MessageHandle;
import ru.tggc.telegrambotframework.annotation.params.Ctx;
import ru.tggc.telegrambotframework.annotation.params.HandleParam;
import ru.tggc.telegrambotframework.annotation.params.MessageParam;
import ru.tggc.telegrambotframework.annotation.params.Username;
import ru.tggc.telegrambotframework.dto.Response;
import ru.tggc.telegrambotframework.dto.UpdateContext;
import ru.tggc.telegrambotframework.dto.UserRole;

import java.util.Locale;

@BotHandler
@RequiredArgsConstructor
public class AdminHandler extends CallbackHandler {
    private final AdminService adminService;
    private final KeyboardFactory keyboardFactory;
    private final HistoryServiceImpl historyService;
    private final UserServiceImpl userServiceImpl;

    @CallbackHandle(value = "admin_menu", canPublic = false, canPrivate = true, requiredRoles = {UserRole.ADMIN, UserRole.SUPER_ADMIN})
    public Response adminMenu(@Ctx UpdateContext ctx) {
        AdminStats stats = adminService.getStats();
        return sendSimpleMessage(
                ctx.chatId(),
                TextBuilder.adminMenu(stats),
                keyboardFactory.getKeyboardInline(KeyboardKey.ADMIN_MENU)
        );
    }

    @CallbackHandle(value = "broadcast", canPublic = false, canPrivate = true, requiredRoles = {UserRole.ADMIN, UserRole.SUPER_ADMIN})
    public Response startBroadcast(@Ctx UpdateContext ctx) {
        historyService.setHistory(ctx, HistoryType.BROADCAST);
        return sendSimpleMessage(ctx.chatId(), "Введите сообщение для рассылки!");
    }

    @MessageHandle(value = "Админка", canPrivate = true, canPublic = false, requiredRoles = {UserRole.ADMIN, UserRole.SUPER_ADMIN})
    public Response openAdmin(@Ctx UpdateContext ctx) {
        AdminStats stats = adminService.getStats();
        return sendSimpleMessage(
                ctx.chatId(),
                TextBuilder.adminMenu(stats),
                keyboardFactory.getKeyboardInline(KeyboardKey.ADMIN_MENU)
        );
    }

    @MessageHandle(value = "block ${username} ${reason}", canPrivate = true, requiredRoles = {UserRole.ADMIN, UserRole.SUPER_ADMIN})
    public Response block(@MessageParam Message message,
                          @Ctx UpdateContext ctx,
                          @HandleParam("reason") String reason,
                          @HandleParam("username") String username,
                          @Username String reporterUsername) {
        userServiceImpl.blockUser(username.toLowerCase(Locale.ROOT).replace("@", ""), reason, reporterUsername);
        return sendSimpleMessage(ctx.chatId(), "Пользователь " + username + " забанен по причине " + reason);
    }
}
