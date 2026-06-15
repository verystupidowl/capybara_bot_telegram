package ru.tggc.botapp.access;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.tggc.botapp.service.impl.UserServiceImpl;
import ru.tggc.telegrambotframework.access.checker.AccessChecker;
import ru.tggc.telegrambotframework.dto.AccessResult;
import ru.tggc.telegrambotframework.dto.Response;
import ru.tggc.telegrambotframework.dto.ResponseBuilder;

import java.lang.reflect.Method;

@Component
@Order(6)
@RequiredArgsConstructor
public class UserBlockedAccessChecker implements AccessChecker {
    private final UserServiceImpl userService;

    @Override
    public AccessResult check(User from, Method method, Chat chat) {
        return userService.getBlockReason(from.username())
                .map(result -> {
                    Response response = ResponseBuilder.to(chat.id())
                            .message("Пользователь " + from.username() + " заблокирован по причине: \n" + result)
                            .build();
                    return AccessResult.deny(response);
                })
                .orElseGet(AccessResult::allow);
    }
}
