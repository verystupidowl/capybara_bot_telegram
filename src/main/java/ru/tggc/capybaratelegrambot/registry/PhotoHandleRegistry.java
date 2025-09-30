package ru.tggc.capybaratelegrambot.registry;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.PhotoHandle;
import ru.tggc.capybaratelegrambot.domain.dto.response.Response;
import ru.tggc.capybaratelegrambot.domain.model.enums.UserRole;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.service.UserService;
import ru.tggc.capybaratelegrambot.utils.UserRateLimiterService;
import ru.tggc.capybaratelegrambot.utils.Utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static ru.tggc.capybaratelegrambot.utils.Utils.throwIfNull;

@Component
@Slf4j
public class PhotoHandleRegistry extends AbstractHandleRegistry<Message> {

    protected PhotoHandleRegistry(ListableBeanFactory beanFactory,
                                  InlineKeyboardCreator inlineKeyboardCreator,
                                  UserService userService,
                                  UserRateLimiterService rateLimiterService) {
        super(beanFactory, inlineKeyboardCreator, userService, rateLimiterService);
    }

    @Override
    protected UserRole[] getRequiredRoles(Method method) {
        return Utils.getOr(
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
    public Response dispatch(Message message) {
        if (message.photo() == null || message.photo().length == 0 || message.animation() == null) {
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
        throwIfNull(method, IllegalStateException::new);

        String template = method.getAnnotation(PhotoHandle.class).value();
        Object[] args = buildArgs(method, message, chat.id(), from.id(), 0, null, message);
        return invokeWithCatch(from, method, beans.get(template), args, chat);
    }
}