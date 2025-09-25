package ru.tggc.capybaratelegrambot.aop;

import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ListableBeanFactory;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.DefaultMessageHandle;
import ru.tggc.capybaratelegrambot.aop.annotation.params.CallbackParam;
import ru.tggc.capybaratelegrambot.aop.annotation.params.ChatId;
import ru.tggc.capybaratelegrambot.aop.annotation.params.Ctx;
import ru.tggc.capybaratelegrambot.aop.annotation.params.HandleParam;
import ru.tggc.capybaratelegrambot.aop.annotation.params.MessageId;
import ru.tggc.capybaratelegrambot.aop.annotation.params.MessageParam;
import ru.tggc.capybaratelegrambot.aop.annotation.params.UserId;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.dto.response.Response;
import ru.tggc.capybaratelegrambot.domain.model.enums.UserRole;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraAlreadyExistsException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraHasNoMoneyException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraNotFoundException;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.service.UserService;
import ru.tggc.capybaratelegrambot.utils.ParamConverter;
import ru.tggc.capybaratelegrambot.utils.Text;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractHandleRegistry<P> {
    protected final Map<String, Method> methods = new ConcurrentHashMap<>();
    protected final Map<String, Object> beans = new ConcurrentHashMap<>();
    protected final Map<String, Pattern> patterns = new ConcurrentHashMap<>();
    protected final ListableBeanFactory beanFactory;
    private final InlineKeyboardCreator inlineKeyboardCreator;
    private final UserService userService;
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
                    if (defaultMethod != null) {
                        throw new IllegalStateException("Должен быть только один @DefaultMessageHandle");
                    }
                    defaultMethod = method;
                    defaultBean = bean;
                    log.info("Registered default message handler: {}.{}",
                            bean.getClass().getSimpleName(), method.getName());
                }
            }
        }
    }

    protected Response invokeWithCatch(String userId, Method method, Object bean, Object[] args, long chatId) {
        Response response;
        try {
            UserRole[] requiredRoles = getRequiredRoles(method);
            if (requiredRoles.length != 0 && !userService.checkRoles(Long.valueOf(userId), requiredRoles)) {
                return null;
            }
            response = (Response) method.invoke(bean, args);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            switch (cause) {
                case CapybaraNotFoundException ex -> {
                    log.info(ex.getMessage(), ex.getChatId());
                    SendMessage message = new SendMessage(chatId, Text.DONT_HAVE_CAPYBARA);
                    message.replyMarkup(inlineKeyboardCreator.takeCapybara());
                    response = Response.ofMessage(message);
                }
                case CapybaraAlreadyExistsException ex -> {
                    log.info(ex.getMessage(), ex.getChatId());
                    response = Response.ofMessage(new SendMessage(chatId, Text.ALREADY_HAVE_CAPYBARA));
                }
                case CapybaraHasNoMoneyException ex -> {
                    String messageToSend = "ur capy has no money(";
                    response = Response.ofMessage(new SendMessage(chatId, messageToSend));
                }
                case CapybaraException ex -> {
                    log.info(ex.getMessage(), ex.getChatId());
                    String messageToSend = ex.getMessageToSend();
                    response = Response.ofMessage(new SendMessage(chatId, Objects.requireNonNullElse(messageToSend, DEFAULT_ERROR_MESSAGE)));
                }
                case NumberFormatException ex -> response = Response.ofMessage(new SendMessage(chatId, "Введи число!"));
                default -> {
                    log.error("Error invoking callback", cause);
                    SendMessage sendMessageToUser = new SendMessage(chatId, DEFAULT_ERROR_MESSAGE);
                    SendMessage sendMessageToAdmin = new SendMessage(ADMIN_ID, buildMessageToAdmin(cause.getMessage()));
                    response = Response.ofMessages(sendMessageToAdmin, sendMessageToUser);
                }
            }
        } catch (Exception e) {
            log.error("Error invoking callback", e);
            response = Response.ofMessage(new SendMessage(chatId, DEFAULT_ERROR_MESSAGE));
        }
        return response;
    }

    protected abstract UserRole[] getRequiredRoles(Method method);

    protected Object[] buildArgs(Method method,
                                 Object update,
                                 Long chatId,
                                 String userId,
                                 int messageId,
                                 Matcher matcher,
                                 P param) {
        return Arrays.stream(method.getParameters())
                .map(parameter -> switch (parameter) {
                    case Parameter p when p.getType().isAssignableFrom(update.getClass()) -> update;
                    case Parameter p when p.isAnnotationPresent(ChatId.class) -> chatId.toString();
                    case Parameter p when p.isAnnotationPresent(UserId.class) -> userId;
                    case Parameter p when p.isAnnotationPresent(Ctx.class) ->
                            new CapybaraContext(chatId.toString(), userId);
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

    protected String buildMessageToAdmin(String message) {
        return LocalDateTime.now() + "\n" + message;
    }
}
