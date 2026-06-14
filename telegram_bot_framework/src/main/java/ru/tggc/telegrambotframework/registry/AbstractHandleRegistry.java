package ru.tggc.telegrambotframework.registry;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import ru.tggc.telegrambotframework.dto.ChatDto;
import ru.tggc.telegrambotframework.dto.Response;
import ru.tggc.telegrambotframework.dto.UserDto;
import ru.tggc.telegrambotframework.dto.UserRole;
import ru.tggc.telegrambotframework.exception.ExceptionHandler;
import ru.tggc.telegrambotframework.service.UserRateLimiterService;
import ru.tggc.telegrambotframework.service.UserService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractHandleRegistry implements HandleRegistry {
    protected Map<String, RegisteredHandler> handlerMap = new ConcurrentHashMap<>();

    private final HandlerScanner handlerScanner;
    private final UserService userService;
    private final UserRateLimiterService rateLimiter;
    private final ExceptionHandler exceptionHandler;

    protected Method defaultMethod;
    protected Object defaultBean;

    protected static final String NOT_IMPLEMENTED_MESSAGE = "Пока не реализовано, следите за новостями!";
    protected static final long ADMIN_ID = 428873987;

    @PostConstruct
    public void init() {
        HandlerRegistryData data = handlerScanner.scan(getHandleAnnotation());

        handlerMap.putAll(data.registeredHandlers());
        defaultMethod = data.defaultMethod();
        defaultBean = data.defaultBean();
    }

    protected Response invokeWithCatch(User from, Method method, Object bean, Object[] args, Chat chat) {
        Response response;
        try {
            Response checkedRequest = checkRequest(from, method, chat);
            if (checkedRequest != null) return checkedRequest;
            rateLimiter.lock(from.id());
            response = (Response) method.invoke(bean, args);
        } catch (Exception e) {
            return exceptionHandler.handleException(e, chat, from);
        }
        return response.andThen(_ -> rateLimiter.unlock(from.id()));
    }

    protected void saveOrUpdateUser(User from, Chat chat) {
        userService.saveOrUpdate(new UserDto(from.id(), from.username()), new ChatDto(chat.id(), chat.title()));
    }

    @Nullable
    private Response checkRequest(User from, Method method, Chat chat) {
        UserRole[] requiredRoles = getRequiredRoles(method);
        if (requiredRoles.length != 0 && !userService.checkRoles(from.id(), requiredRoles)) {
            return Response.empty();
        }
        boolean isPrivateMessage = from.id().equals(chat.id());
        boolean canRequestBePrivate = canRequestBePrivate(method);
        boolean canRequestBePublic = canRequestBePublic(method);
        if ((isPrivateMessage && canRequestBePrivate) || (!isPrivateMessage && canRequestBePublic)) {
            return rateLimiter.checkUser(from, chat);
        }
        return Response.empty();
    }

    protected abstract boolean canRequestBePublic(Method method);

    protected abstract boolean canRequestBePrivate(Method method);

    protected abstract UserRole[] getRequiredRoles(Method method);

    protected abstract Class<? extends Annotation> getHandleAnnotation();
}
