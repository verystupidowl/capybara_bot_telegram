package ru.tggc.telegrambotframework.access.checker;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tggc.telegrambotframework.dto.AccessResult;
import ru.tggc.telegrambotframework.dto.Response;

import java.lang.reflect.Method;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GlobalAccessChecker {
    private final List<AccessChecker> accessCheckers;

    public Response check(User from, Method method, Chat chat) {
        for (AccessChecker accessChecker : accessCheckers){
            AccessResult accessResult = accessChecker.check(from, method, chat);
            if (!accessResult.allowed()) {
                return accessResult.response();
            }
        }
        return null;
    }
}
