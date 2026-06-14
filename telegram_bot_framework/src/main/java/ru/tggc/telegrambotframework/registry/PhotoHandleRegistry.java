package ru.tggc.telegrambotframework.registry;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.tggc.telegrambotframework.annotation.handle.PhotoHandle;
import ru.tggc.telegrambotframework.dto.Response;
import ru.tggc.telegrambotframework.dto.UserRole;
import ru.tggc.telegrambotframework.exception.ExceptionHandler;
import ru.tggc.telegrambotframework.registry.resolver.HandlerArgumentResolver;
import ru.tggc.telegrambotframework.registry.resolver.HandlerCtx;
import ru.tggc.telegrambotframework.service.UserRateLimiterService;
import ru.tggc.telegrambotframework.service.UserService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static ru.tggc.telegrambotframework.util.Utils.getOrElse;
import static ru.tggc.telegrambotframework.util.Utils.throwIfNull;

@Component
@Slf4j
public class PhotoHandleRegistry extends AbstractHandleRegistry {
    private final HandlerArgumentResolver handlerArgumentResolver;

    protected PhotoHandleRegistry(UserService userService,
                                  UserRateLimiterService rateLimiterService,
                                  ExceptionHandler exceptionHandler,
                                  HandlerScanner handlerScanner,
                                  HandlerArgumentResolver handlerArgumentResolver) {
        super(handlerScanner, userService, rateLimiterService, exceptionHandler);
        this.handlerArgumentResolver = handlerArgumentResolver;
    }

    @Override
    protected boolean canRequestBePublic(Method method) {
        return method.getAnnotation(PhotoHandle.class).canPublic();
    }

    @Override
    protected boolean canRequestBePrivate(Method method) {
        return method.getAnnotation(PhotoHandle.class).canPrivate();
    }

    @Override
    protected UserRole[] getRequiredRoles(Method method) {
        return getOrElse(
                method.getAnnotation(PhotoHandle.class),
                PhotoHandle::requiredRoles,
                new UserRole[0]
        );
    }

    @Override
    protected Class<? extends Annotation> getHandleAnnotation() {
        return PhotoHandle.class;
    }

    @Override
    public Response dispatch(Update update) {
        Message message = update.message();

        if (message.photo() == null || message.photo().length == 0) {
            log.warn("PhotoHandleRegistry.dispatch called, but no photo in message");
            return null;
        }

        Method method = handlerMap.values().stream()
                .map(RegisteredHandler::getMethod)
                .filter(m -> {
                    String template = m.getAnnotation(PhotoHandle.class).value();
                    return template.equals("update_photo");
                })
                .findFirst()
                .orElse(null);

        Chat chat = message.chat();
        User from = message.from();

        saveOrUpdateUser(from, chat);

        throwIfNull(method, IllegalStateException::new);

        String template = method.getAnnotation(PhotoHandle.class).value();

        HandlerCtx ctx = new HandlerCtx(
                update,
                chat.id(),
                from,
                0,
                null
        );
        Object[] args = handlerArgumentResolver.resolve(method, ctx);
        return invokeWithCatch(from, method, handlerMap.get(template).getBean(), args, chat);
    }

    @Override
    public boolean canHandle(Update update) {
        return update.message() != null
                && update.message().photo() != null
                && update.message().photo().length > 0;
    }
}