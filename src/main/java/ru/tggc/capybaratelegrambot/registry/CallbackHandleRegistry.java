package ru.tggc.capybaratelegrambot.registry;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.annotation.handle.CallbackHandle;
import ru.tggc.capybaratelegrambot.domain.model.enums.UserRole;
import ru.tggc.capybaratelegrambot.domain.response.Response;
import ru.tggc.capybaratelegrambot.exceptions.handler.ExceptionHandler;
import ru.tggc.capybaratelegrambot.service.UserRateLimiterService;
import ru.tggc.capybaratelegrambot.service.UserService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class CallbackHandleRegistry extends AbstractHandleRegistry {
    private final ExceptionHandler exceptionHandler;

    protected CallbackHandleRegistry(ListableBeanFactory beanFactory,
                                     UserService userService,
                                     UserRateLimiterService rateLimiterService,
                                     ExceptionHandler exceptionHandler) {
        super(beanFactory, userService, rateLimiterService, exceptionHandler);
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    protected boolean canRequestBePublic(Method method) {
        return method.getAnnotation(CallbackHandle.class).canPublic();
    }

    @Override
    protected boolean canRequestBePrivate(Method method) {
        return method.getAnnotation(CallbackHandle.class).canPrivate();
    }

    @Override
    protected UserRole[] getRequiredRoles(Method method) {
        return method.getAnnotation(CallbackHandle.class).requiredRoles();
    }

    @Override
    protected Class<? extends Annotation> getHandleAnnotation() {
        return CallbackHandle.class;
    }

    @Override
    public Response dispatch(Update update) {
        CallbackQuery query = update.callbackQuery();
        String data = query.data();
        Method method = methods.values().stream()
                .filter(m -> {
                    String template = m.getAnnotation(CallbackHandle.class).value();
                    if (template.equals(data)) return true;
                    Pattern p = patterns.get(template);
                    return p != null && p.matcher(data).matches();
                })
                .findFirst()
                .orElse(null);
        Chat chat = query.maybeInaccessibleMessage().chat();
        User from = query.from();
        long chatId = chat.id();
        int messageId = query.maybeInaccessibleMessage().messageId();

        saveOrUpdateUser(from, chat);

        if (method == null) {
            log.warn("Unknown callback: {}", data);
            String message = exceptionHandler.buildMessageToAdmin("Unknown callback: " + data, chat, from);
            SendMessage sendMessageToUser = new SendMessage(chatId, NOT_IMPLEMENTED_MESSAGE);
            SendMessage sendMessageToAdmin = new SendMessage(ADMIN_ID, message);
            return Response.ofAll(sendMessageToAdmin, sendMessageToUser);
        }
        log.info("message {} from {}", query.data(), from.username());

        String template = method.getAnnotation(CallbackHandle.class).value();
        Matcher matcher = patterns.containsKey(template) ? patterns.get(template).matcher(data) : null;

        Object[] args = buildArgs(method, query, chatId, from, messageId, matcher);
        return invokeWithCatch(from, method, beans.get(template), args, chat);
    }

    @Override
    public boolean canHandle(Update update) {
        return update.callbackQuery() != null;
    }
}
