package ru.tggc.telegrambotframework.registry;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.tggc.telegrambotframework.access.checker.GlobalAccessChecker;
import ru.tggc.telegrambotframework.annotation.handle.MessageHandle;
import ru.tggc.telegrambotframework.dto.Response;
import ru.tggc.telegrambotframework.dto.ResponseBuilder;
import ru.tggc.telegrambotframework.dto.UpdateContext;
import ru.tggc.telegrambotframework.exception.ExceptionHandler;
import ru.tggc.telegrambotframework.registry.resolver.HandlerArgumentResolver;
import ru.tggc.telegrambotframework.registry.resolver.HandlerCtx;
import ru.tggc.telegrambotframework.service.HistoryService;
import ru.tggc.telegrambotframework.service.UserRateLimiterService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
@Slf4j
public class MessageHandleRegistry extends AbstractHandleRegistry {
    private static final long BOT_ID = 6653668731L;

    private final HistoryService historyService;
    private final HandlerArgumentResolver handlerArgumentResolver;

    protected MessageHandleRegistry(HandlerScanner handlerScanner,
                                    UserRateLimiterService rateLimiter,
                                    ExceptionHandler exceptionHandler,
                                    GlobalAccessChecker globalAccessChecker,
                                    HandlerArgumentResolver handlerArgumentResolver,
                                    HistoryService historyService) {
        super(handlerScanner, rateLimiter, exceptionHandler, globalAccessChecker);
        this.historyService = historyService;
        this.handlerArgumentResolver = handlerArgumentResolver;
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

        if (isBotAdded(message)) {
            return handleGreetings(message);
        }

        if (method == null) {
            if (defaultMethod == null) {
                log.warn("Unknown message: {}", text);
            } else {
                UpdateContext ctx = new UpdateContext(chat.id(), from.id(), message.messageId());
                if (historyService.contains(ctx)) {
                    HandlerCtx handlerCtx = new HandlerCtx(
                            update,
                            chat,
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
                chat,
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
                .message("Hello, " + m.from().username() + "!")
                .build();
    }

    @Override
    public boolean canHandle(Update update) {
        return update.message() != null
                && (update.message().photo() == null
                || update.message().photo().length == 0);
    }
}
