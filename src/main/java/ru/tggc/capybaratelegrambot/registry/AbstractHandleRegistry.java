package ru.tggc.capybaratelegrambot.registry;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.ListableBeanFactory;
import ru.tggc.capybaratelegrambot.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.annotation.handle.DefaultMessageHandle;
import ru.tggc.capybaratelegrambot.annotation.params.CallbackParam;
import ru.tggc.capybaratelegrambot.annotation.params.ChatId;
import ru.tggc.capybaratelegrambot.annotation.params.Ctx;
import ru.tggc.capybaratelegrambot.annotation.params.HandleParam;
import ru.tggc.capybaratelegrambot.annotation.params.MessageId;
import ru.tggc.capybaratelegrambot.annotation.params.MessageParam;
import ru.tggc.capybaratelegrambot.annotation.params.UserId;
import ru.tggc.capybaratelegrambot.annotation.params.Username;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.model.enums.UserRole;
import ru.tggc.capybaratelegrambot.domain.response.Response;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraAlreadyExistsException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraHasNoMoneyException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraNotFoundException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraTiredException;
import ru.tggc.capybaratelegrambot.exceptions.UserNotFoundException;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.service.UserService;
import ru.tggc.capybaratelegrambot.utils.ParamConverter;
import ru.tggc.capybaratelegrambot.utils.Text;
import ru.tggc.capybaratelegrambot.service.UserRateLimiterService;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.tggc.capybaratelegrambot.utils.Utils.getOr;
import static ru.tggc.capybaratelegrambot.utils.Utils.ifPresent;
import static ru.tggc.capybaratelegrambot.utils.Utils.throwIf;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractHandleRegistry<U> implements HandleRegistry<U> {
    protected final Map<String, Method> methods = new ConcurrentHashMap<>();
    protected final Map<String, Object> beans = new ConcurrentHashMap<>();
    protected final Map<String, Pattern> patterns = new ConcurrentHashMap<>();

    protected final ListableBeanFactory beanFactory;
    private final InlineKeyboardCreator inlineKeyboardCreator;
    private final UserService userService;
    private final UserRateLimiterService rateLimiter;

    protected Method defaultMethod;
    protected Object defaultBean;

    protected static final String DEFAULT_ERROR_MESSAGE = "Непредвиденная ошибка";
    protected static final String NOT_IMPLEMENTED_MESSAGE = "Пока не реализовано, следите за новостями!";
    protected static final long ADMIN_ID = 428873987;

    @PostConstruct
    @SneakyThrows
    public void init() {
        Map<String, Object> handlerBeans = beanFactory.getBeansWithAnnotation(BotHandler.class);
        for (Object bean : handlerBeans.values()) {
            for (Method method : bean.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(getHandleAnnotation())) {
                    Annotation ann = method.getAnnotation(getHandleAnnotation());
                    String key = (String) ann.annotationType()
                            .getMethod("value")
                            .invoke(ann);
                    methods.put(key, method);
                    beans.put(key, bean);
                    if (key.contains("${")) {
                        String regex = key.replaceAll("\\$\\{(\\w+)}", "(?<$1>.+)");
                        patterns.put(key, Pattern.compile(regex));
                    }
                    log.info("Registered handler '{}' -> {}.{}",
                            key, bean.getClass().getSimpleName(), method.getName());
                } else if (method.isAnnotationPresent(DefaultMessageHandle.class)) {
                    throwIf(defaultMethod != null, () -> new IllegalStateException("Должен быть только один @DefaultMessageHandle"));
                    defaultMethod = method;
                    defaultBean = bean;
                    log.info("Registered default message handler: {}.{}",
                            bean.getClass().getSimpleName(), method.getName());
                }
            }
        }
    }

    protected Response invokeWithCatch(User from, Method method, Object bean, Object[] args, Chat chat) {
        Response response;
        long chatId = chat.id();
        try {
            Response checkedRequest = checkRequest(from, method, chat);
            if (checkedRequest != null) return checkedRequest;
            rateLimiter.lock(from.id());
            response = (Response) method.invoke(bean, args);
        } catch (InvocationTargetException | CompletionException e) {
            Throwable cause = unwrap(e);
            switch (cause) {
                case CapybaraNotFoundException ex -> {
                    log.info(ex.getMessage(), chatId);
                    SendMessage message = new SendMessage(chatId, Text.DONT_HAVE_CAPYBARA);
                    message.replyMarkup(inlineKeyboardCreator.takeCapybara());
                    response = Response.of(message);
                }
                case UserNotFoundException ex -> {
                    log.info(ex.getMessage(), chatId);
                    SendMessage message = new SendMessage(chatId, Text.DONT_HAVE_CAPYBARA);
                    message.replyMarkup(inlineKeyboardCreator.takeCapybara());
                    response = Response.of(message);
                }
                case CapybaraAlreadyExistsException ex -> {
                    log.info(ex.getMessage(), chatId);
                    response = Response.of(new SendMessage(chatId, Text.ALREADY_HAVE_CAPYBARA));
                }
                case CapybaraHasNoMoneyException ex -> {
                    log.info(ex.getMessage());
                    String messageToSend = Text.NO_MONEY;
                    response = Response.of(new SendMessage(chatId, messageToSend));
                }
                case CapybaraTiredException ex -> {
                    SendMessage sm = new SendMessage(chatId, ex.getMessage());
                    ifPresent(ex.getMarkup(), sm::replyMarkup);
                    response = Response.of(sm);
                }
                case CapybaraException ex -> {
                    log.info(ex.getMessage(), chatId);
                    String messageToSend = ex.getMessageToSend();
                    SendMessage sm = new SendMessage(chatId, Objects.requireNonNullElse(messageToSend, DEFAULT_ERROR_MESSAGE));
                    ifPresent(ex.getMarkup(), sm::replyMarkup);
                    response = Response.of(sm);
                }
                case NumberFormatException ignored -> response = Response.of(new SendMessage(chatId, "Введи число!"));
                default -> {
                    log.error("Error invoking callback", cause);
                    SendMessage sendMessageToUser = new SendMessage(chatId, DEFAULT_ERROR_MESSAGE);
                    SendMessage sendMessageToAdmin = new SendMessage(ADMIN_ID, buildMessageToAdmin(cause.getMessage(), chat, from));
                    response = Response.ofAll(sendMessageToAdmin, sendMessageToUser);
                }
            }
        } catch (Exception e) {
            log.error("Error invoking callback", e);
            response = Response.of(new SendMessage(chatId, DEFAULT_ERROR_MESSAGE));
        }
        return response.andThen(bot -> {
            rateLimiter.unlock(from.id());
            return CompletableFuture.completedFuture(null);
        });
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

    protected Object[] buildArgs(Method method,
                                 Object update,
                                 long chatId,
                                 User from,
                                 int messageId,
                                 Matcher matcher,
                                 U param) {
        return Arrays.stream(method.getParameters())
                .map(parameter -> switch (parameter) {
                    case Parameter p when p.getType().isAssignableFrom(update.getClass()) -> update;
                    case Parameter p when p.isAnnotationPresent(ChatId.class) -> chatId;
                    case Parameter p when p.isAnnotationPresent(UserId.class) -> from.id();
                    case Parameter p when p.isAnnotationPresent(Username.class) -> from.username();
                    case Parameter p when p.isAnnotationPresent(Ctx.class) ->
                            new CapybaraContext(chatId, from.id(), messageId);
                    case Parameter p when p.isAnnotationPresent(CallbackParam.class)
                            || p.isAnnotationPresent(MessageParam.class) -> param;
                    case Parameter p when p.isAnnotationPresent(MessageId.class) -> messageId;
                    case Parameter p when p.isAnnotationPresent(HandleParam.class)
                            && matcher != null && matcher.matches() -> {
                        String name = p.getAnnotation(HandleParam.class).value();
                        String raw = matcher.group(name);
                        yield ParamConverter.convert(raw, p.getType());
                    }
                    default -> null;
                })
                .toArray();
    }

    protected abstract Class<? extends Annotation> getHandleAnnotation();

    protected String buildMessageToAdmin(String message, Chat chat, User from) {
        return LocalDateTime.now() + "\n" + from.username() + "\n" + getOr(chat.title(), Function.identity(), "Личка") + "\n" + message;
    }

    private Throwable unwrap(Throwable e) {
        if (e instanceof InvocationTargetException ite && ite.getCause() != null) {
            return unwrap(ite.getCause());
        }
        if (e instanceof CompletionException ce && ce.getCause() != null) {
            return unwrap(ce.getCause());
        }
        return e;
    }
}
