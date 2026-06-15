package ru.tggc.telegrambotframework.registry;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.tggc.telegrambotframework.access.checker.GlobalAccessChecker;
import ru.tggc.telegrambotframework.annotation.handle.CallbackHandle;
import ru.tggc.telegrambotframework.dto.Response;
import ru.tggc.telegrambotframework.exception.ExceptionHandler;
import ru.tggc.telegrambotframework.registry.resolver.HandlerArgumentResolver;
import ru.tggc.telegrambotframework.registry.resolver.HandlerCtx;
import ru.tggc.telegrambotframework.service.UserRateLimiterService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class CallbackHandleRegistry extends AbstractHandleRegistry {
    private final ExceptionHandler exceptionHandler;
    private final HandlerArgumentResolver handlerArgumentResolver;

    public CallbackHandleRegistry(HandlerScanner handlerScanner,
                                  UserRateLimiterService rateLimiter,
                                  ExceptionHandler exceptionHandler,
                                  GlobalAccessChecker globalAccessChecker,
                                  HandlerArgumentResolver handlerArgumentResolver) {
        super(handlerScanner, rateLimiter, exceptionHandler, globalAccessChecker);
        this.exceptionHandler = exceptionHandler;
        this.handlerArgumentResolver = handlerArgumentResolver;
    }

    @Override
    protected Class<? extends Annotation> getHandleAnnotation() {
        return CallbackHandle.class;
    }

    @Override
    public Response dispatch(Update update) {
        CallbackQuery query = update.callbackQuery();
        String data = query.data();
        Method method = handlerMap.values().stream()
                .map(RegisteredHandler::getMethod)
                .filter(m -> {
                    String template = m.getAnnotation(CallbackHandle.class).value();
                    if (template.equals(data)) return true;
                    Pattern p = handlerMap.get(template).getPattern();
                    return p != null && p.matcher(data).matches();
                })
                .findFirst()
                .orElse(null);
        Chat chat = query.maybeInaccessibleMessage().chat();
        User from = query.from();
        long chatId = chat.id();
        int messageId = query.maybeInaccessibleMessage().messageId();

        if (method == null) {
            log.warn("Unknown callback: {}", data);
            String message = exceptionHandler.buildMessageToAdmin("Unknown callback: " + data, chat, from);
            SendMessage sendMessageToUser = new SendMessage(chatId, NOT_IMPLEMENTED_MESSAGE);
            SendMessage sendMessageToAdmin = new SendMessage(ADMIN_ID, message);
            return Response.ofAll(sendMessageToAdmin, sendMessageToUser);
        }
        log.info("message {} from {}", query.data(), from.username());

        String template = method.getAnnotation(CallbackHandle.class).value();
        Matcher matcher = Optional.ofNullable(handlerMap.get(template))
                .map(RegisteredHandler::getPattern)
                .map(p -> p.matcher(data))
                .orElse(null);

        HandlerCtx ctx = new HandlerCtx(
                update,
                chat,
                from,
                messageId,
                matcher
        );
        Object[] args = handlerArgumentResolver.resolve(method, ctx);
        return invokeWithCatch(from, method, handlerMap.get(template).getBean(), args, chat);
    }

    @Override
    public boolean canHandle(Update update) {
        return update.callbackQuery() != null;
    }
}
