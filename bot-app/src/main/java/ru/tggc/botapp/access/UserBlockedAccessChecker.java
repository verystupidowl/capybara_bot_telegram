package ru.tggc.botapp.access;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.tggc.botapp.formatter.msgkey.AdminMsgKey;
import ru.tggc.botapp.service.impl.UserServiceImpl;
import ru.tggc.telegrambotcore.access.checker.AccessChecker;
import ru.tggc.telegrambotcore.dto.AccessResult;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.ResponseBuilder;
import ru.tggc.telegrambotcore.formatter.FormatService;

import java.lang.reflect.Method;

@Component
@Order(6)
@RequiredArgsConstructor
public class UserBlockedAccessChecker implements AccessChecker {
    private final UserServiceImpl userService;
    private final FormatService formatService;

    @NotNull
    @Override
    public AccessResult check(User from, @NotNull Method method, @NotNull Chat chat) {
        return userService.getBlockReason(from.username())
                .map(result -> {
                    String message = formatService.get(
                            AdminMsgKey.BLOCK_MESSAGE,
                            from.username(),
                            result.getReporter(),
                            result.getReason()
                    );
                    Response response = ResponseBuilder.to(chat.id())
                            .message(message)
                            .build();
                    return AccessResult.deny(response);
                })
                .orElseGet(AccessResult::allow);
    }
}
