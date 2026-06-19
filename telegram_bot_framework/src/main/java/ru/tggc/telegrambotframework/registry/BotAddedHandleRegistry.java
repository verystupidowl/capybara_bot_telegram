package ru.tggc.telegrambotframework.registry;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.tggc.telegrambotframework.access.checker.GlobalAccessChecker;
import ru.tggc.telegrambotframework.annotation.handle.BotAddedHandle;
import ru.tggc.telegrambotframework.dto.Response;
import ru.tggc.telegrambotframework.exception.ExceptionHandler;
import ru.tggc.telegrambotframework.registry.resolver.HandlerArgumentResolver;
import ru.tggc.telegrambotframework.registry.resolver.HandlerCtx;
import ru.tggc.telegrambotframework.service.UserRateLimiterService;
import ru.tggc.telegrambotframework.service.UserService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

@Component
@Slf4j
public class BotAddedHandleRegistry extends AbstractHandleRegistry {
    @Value("${bot.id}")
    private long botId;

    private final HandlerArgumentResolver handlerArgumentResolver;

    public BotAddedHandleRegistry(HandlerScanner handlerScanner,
                                  UserRateLimiterService rateLimiter,
                                  ExceptionHandler exceptionHandler,
                                  GlobalAccessChecker globalAccessChecker,
                                  UserService userService,
                                  HandlerArgumentResolver handlerArgumentResolver) {
        super(handlerScanner, rateLimiter, exceptionHandler, globalAccessChecker, userService);
        this.handlerArgumentResolver = handlerArgumentResolver;
    }

    @Override
    protected Class<? extends Annotation> getHandleAnnotation() {
        return BotAddedHandle.class;
    }

    @Override
    public Response dispatch(Update update) {
        Message message = update.message();

        Method method = handlerMap.values().stream()
                .map(RegisteredHandler::getMethod)
                .filter(m -> {
                    String template = m.getAnnotation(BotAddedHandle.class).value();
                    return template.equals("bot_added");
                })
                .findFirst()
                .orElse(null);
        Chat chat = message.chat();
        User from = message.from();
        int messageId = message.messageId();

        if (method == null) {
            throw new RuntimeException("method is null");
        }

        saveOrUpdateUser(from, chat);

        HandlerCtx ctx = new HandlerCtx(
                update,
                chat,
                from,
                messageId,
                null
        );
        Object[] args = handlerArgumentResolver.resolve(method, ctx);
        return invokeWithCatch(from, method, handlerMap.get("bot_added").getBean(), args, chat);
    }

    @Override
    public boolean canHandle(Update update) {
        return Stream.ofNullable(update.message())
                .map(Message::newChatMembers)
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .anyMatch(member -> member.id() == botId);
    }
}
