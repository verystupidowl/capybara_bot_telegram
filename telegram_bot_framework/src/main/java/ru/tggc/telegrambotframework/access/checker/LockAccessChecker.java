package ru.tggc.telegrambotframework.access.checker;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.tggc.telegrambotframework.dto.AccessResult;
import ru.tggc.telegrambotframework.dto.Response;
import ru.tggc.telegrambotframework.service.UserRateLimiterService;

import java.lang.reflect.Method;

@Component
@Order(3)
@RequiredArgsConstructor
public class LockAccessChecker implements AccessChecker {
    private final UserRateLimiterService rateLimiterService;

    @Override
    public AccessResult check(User from, Method method, Chat chat) {
        return rateLimiterService.isLocked(from.id()) ? AccessResult.deny(Response.empty()) : AccessResult.allow();
    }
}
