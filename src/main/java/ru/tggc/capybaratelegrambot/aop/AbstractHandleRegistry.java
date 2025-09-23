package ru.tggc.capybaratelegrambot.aop;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ListableBeanFactory;
import ru.tggc.capybaratelegrambot.aop.annotation.CheckType;
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
import ru.tggc.capybaratelegrambot.exceptions.CapybaraAlreadyExistsException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraHasNoMoneyException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraNotFoundException;
import ru.tggc.capybaratelegrambot.utils.ParamConverter;
import ru.tggc.capybaratelegrambot.utils.Text;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public abstract class AbstractHandleRegistry<P> {
    protected final Map<String, Method> methods = new ConcurrentHashMap<>();
    protected final Map<String, Object> beans = new ConcurrentHashMap<>();
    protected final Map<String, Pattern> patterns = new ConcurrentHashMap<>();
    protected final TelegramBot bot;
    protected final ListableBeanFactory beanFactory;
    protected Method defaultMethod;
    protected Object defaultBean;

    protected AbstractHandleRegistry(TelegramBot bot, ListableBeanFactory beanFactory) {
        this.bot = bot;
        this.beanFactory = beanFactory;
    }

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

    protected void invokeWithCatch(Method method, Object bean, Object[] args, String chatId) {
        CheckType checkType = getCheckType(method);
        try {
            method.invoke(bean, args);
        } catch (CapybaraNotFoundException e) {
            if (checkType == CheckType.CHECK_NOT_EXISTS) {
                log.info(e.getMessage(), e.getChatId());
                bot.execute(new SendMessage(e.getChatId(), Text.DONT_HAVE_CAPYBARA));
            }
        } catch (CapybaraAlreadyExistsException e) {
            if (checkType == CheckType.CHECK_EXISTS) {
                log.info(e.getMessage(), e.getChatId());
                bot.execute(new SendMessage(e.getChatId(), Text.ALREADY_HAVE_CAPYBARA));
            }
        } catch (CapybaraHasNoMoneyException e) {
            String messageToSend = "ur capy has no money(";
            bot.execute(new SendMessage(chatId, messageToSend));
        } catch (CapybaraException e) {
            log.info(e.getMessage(), e.getChatId());
            String messageToSend = e.getMessageToSend();
            if (messageToSend != null) {
                bot.execute(new SendMessage(chatId, messageToSend));
            }
        } catch (Exception e) {
            log.error("Error invoking callback", e);
        }
    }

    protected Object[] buildArgs(Method method,
                                 Object update,
                                 String chatId,
                                 String userId,
                                 int messageId,
                                 Matcher matcher,
                                 P param) {
        return Arrays.stream(method.getParameters())
                .map(parameter -> switch (parameter) {
                    case Parameter p when p.getType().isAssignableFrom(update.getClass()) -> update;
                    case Parameter p when p.isAnnotationPresent(ChatId.class) -> chatId;
                    case Parameter p when p.isAnnotationPresent(UserId.class) -> userId;
                    case Parameter p when p.isAnnotationPresent(Ctx.class) -> new CapybaraContext(chatId, userId);
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

    protected abstract CheckType getCheckType(Method method);
}
