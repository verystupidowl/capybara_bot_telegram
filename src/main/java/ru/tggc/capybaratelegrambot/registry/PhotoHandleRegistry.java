package ru.tggc.capybaratelegrambot.registry;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.annotation.handle.PhotoHandle;
import ru.tggc.capybaratelegrambot.domain.model.enums.UserRole;
import ru.tggc.capybaratelegrambot.domain.response.Response;
import ru.tggc.capybaratelegrambot.exceptions.handler.ExceptionHandler;
import ru.tggc.capybaratelegrambot.service.UserRateLimiterService;
import ru.tggc.capybaratelegrambot.service.UserService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static ru.tggc.capybaratelegrambot.utils.Utils.getOrElse;
import static ru.tggc.capybaratelegrambot.utils.Utils.throwIfNull;

@Component
@Slf4j
public class PhotoHandleRegistry extends AbstractHandleRegistry {

    protected PhotoHandleRegistry(ListableBeanFactory beanFactory,
                                  UserService userService,
                                  UserRateLimiterService rateLimiterService,
                                  ExceptionHandler exceptionHandler) {
        super(beanFactory, userService, rateLimiterService, exceptionHandler);
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

        Method method = methods.values().stream()
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
        Object[] args = buildArgs(method, message, chat.id(), from, 0, null);
        return invokeWithCatch(from, method, beans.get(template), args, chat);
    }

    @Override
    public boolean canHandle(Update update) {
        return update.message() != null
                && update.message().photo() != null
                && update.message().photo().length > 0;
    }
}