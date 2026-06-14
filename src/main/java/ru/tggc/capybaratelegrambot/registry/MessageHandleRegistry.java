package ru.tggc.capybaratelegrambot.registry;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.annotation.handle.MessageHandle;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.model.enums.UserRole;
import ru.tggc.capybaratelegrambot.domain.response.Response;
import ru.tggc.capybaratelegrambot.domain.response.ResponseBuilder;
import ru.tggc.capybaratelegrambot.exceptions.handler.ExceptionHandler;
import ru.tggc.capybaratelegrambot.registry.resolver.HandlerArgumentResolver;
import ru.tggc.capybaratelegrambot.registry.resolver.HandlerCtx;
import ru.tggc.capybaratelegrambot.service.HistoryService;
import ru.tggc.capybaratelegrambot.service.UserRateLimiterService;
import ru.tggc.capybaratelegrambot.service.UserService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.tggc.capybaratelegrambot.utils.Utils.getOrElse;

@Component
@Slf4j
public class MessageHandleRegistry extends AbstractHandleRegistry {
    private static final long BOT_ID = 6653668731L;

    private final HistoryService historyService;
    private final HandlerArgumentResolver handlerArgumentResolver;

    protected MessageHandleRegistry(UserService userService,
                                    HistoryService historyService,
                                    UserRateLimiterService rateLimiterService,
                                    ExceptionHandler exceptionHandler,
                                    HandlerScanner handlerScanner,
                                    HandlerArgumentResolver handlerArgumentResolver) {
        super(handlerScanner, userService, rateLimiterService, exceptionHandler);
        this.historyService = historyService;
        this.handlerArgumentResolver = handlerArgumentResolver;
    }

    @Override
    protected boolean canRequestBePublic(Method method) {
        return getOrElse(method.getAnnotation(MessageHandle.class), MessageHandle::canPublic, true);
    }

    @Override
    protected boolean canRequestBePrivate(Method method) {
        return getOrElse(method.getAnnotation(MessageHandle.class), MessageHandle::canPrivate, false);
    }

    @Override
    protected UserRole[] getRequiredRoles(Method method) {
        return getOrElse(
                method.getAnnotation(MessageHandle.class),
                MessageHandle::requiredRoles,
                new UserRole[0]
        );
    }

    @Override
    protected Class<? extends Annotation> getHandleAnnotation() {
        return MessageHandle.class;
    }

    @Override
    public Response dispatch(Update update) {
        Message message = update.message();

        if (message.text() == null) {
            return null;
        }
        String text = message.text().toLowerCase();
        Method method = handlerMap.values().stream()
                .map(RegisteredHandler::getMethod)
                .filter(m -> {
                    String template = m.getAnnotation(MessageHandle.class).value();
                    if (template.toLowerCase(Locale.ROOT).equals(text)) return true;
                    Pattern p = handlerMap.get(template).getPattern();
                    return p != null && p.matcher(text).matches();
                })
                .findFirst()
                .orElse(null);

        Chat chat = message.chat();
        User from = message.from();
        Response response = Response.empty();

        saveOrUpdateUser(from, chat);

        if (isBotAdded(message)) {
            return handleGreetings(message);
        }

        if (method == null) {
            if (defaultMethod == null) {
                log.warn("Unknown message: {}", text);
            } else {
                CapybaraContext ctx = new CapybaraContext(chat.id(), from.id(), message.messageId());
                if (historyService.contains(ctx)) {
                    HandlerCtx handlerCtx = new HandlerCtx(
                            update,
                            chat.id(),
                            from,
                            0,
                            null
                    );
                    Object[] args = handlerArgumentResolver.resolve(defaultMethod, handlerCtx);
                    response = invokeWithCatch(from, defaultMethod, defaultBean, args, chat);
                }
            }
            return response;
        }
        log.info("message {} from {}", message.text(), from.username());

        String template = method.getAnnotation(MessageHandle.class).value();
        Matcher matcher = Optional.ofNullable(handlerMap.get(template))
                .map(RegisteredHandler::getPattern)
                .map(p -> p.matcher(text))
                .orElse(null);

        HandlerCtx ctx = new HandlerCtx(
                update,
                chat.id(),
                from,
                0,
                matcher
        );
        Object[] args = handlerArgumentResolver.resolve(method, ctx);
        return invokeWithCatch(from, method, handlerMap.get(template).getBean(), args, chat);
    }

    private boolean isBotAdded(Message m) {
        return m.newChatMembers() != null &&
                Arrays.stream(m.newChatMembers()).anyMatch(u -> u.id().equals(BOT_ID));
    }

    private Response handleGreetings(Message m) {
        return ResponseBuilder.to(m.chat().id())
                .message("Привет! Я капибара!")
                .build();
    }

    @Override
    public boolean canHandle(Update update) {
        return update.message() != null
                && (update.message().photo() == null
                || update.message().photo().length == 0);
    }
}
