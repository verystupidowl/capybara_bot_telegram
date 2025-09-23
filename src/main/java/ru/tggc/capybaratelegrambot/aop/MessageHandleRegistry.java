package ru.tggc.capybaratelegrambot.aop;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.aop.annotation.CheckType;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.MessageHandle;
import ru.tggc.capybaratelegrambot.domain.dto.response.Response;
import ru.tggc.capybaratelegrambot.domain.dto.response.TextResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class MessageHandleRegistry extends AbstractHandleRegistry<Message> {

    protected MessageHandleRegistry(TelegramBot bot, ListableBeanFactory beanFactory) {
        super(bot, beanFactory);
    }

    @Override
    protected Class<? extends Annotation> getHandleAnnotation() {
        return MessageHandle.class;
    }

    @Override
    protected CheckType getCheckType(Method method) {
        return method.getAnnotation(MessageHandle.class).checkType();
    }


    public void dispatch(Message message) {
        String text = message.text();
        Method method = methods.values().stream()
                .filter(m -> {
                    String template = m.getAnnotation(MessageHandle.class).value();
                    if (template.toLowerCase(Locale.ROOT).equals(text)) return true;
                    Pattern p = patterns.get(template);
                    return p != null && p.matcher(text).matches();
                })
                .findFirst()
                .orElse(null);

        String chatId = message.chat().id().toString();
        String userId = message.from().id().toString();

        if (method == null) {
            if (defaultMethod == null) {
                log.warn("Unknown message: {}", text);
            } else {
                Object[] args = buildArgs(defaultMethod, message, chatId, userId, 0, null, message);
                invokeWithCatch(defaultMethod, defaultBean, args, chatId);
            }
            return;
        }


        String template = method.getAnnotation(MessageHandle.class).value();
        Matcher matcher = patterns.containsKey(template) ? patterns.get(template).matcher(text) : null;

        Object[] args = buildArgs(method, message, chatId, userId, 0, matcher, message);
        invokeWithCatch(method, beans.get(template), args, chatId);
    }
}
