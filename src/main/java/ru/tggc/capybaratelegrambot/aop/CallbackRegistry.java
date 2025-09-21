package ru.tggc.capybaratelegrambot.aop;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.aop.annotation.CheckType;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.CallbackHandle;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class CallbackRegistry extends AbstractHandleRegistry<CallbackQuery> {

    protected CallbackRegistry(TelegramBot bot, ListableBeanFactory beanFactory) {
        super(bot, beanFactory);
    }

    @Override
    protected Class<? extends Annotation> getHandleAnnotation() {
        return CallbackHandle.class;
    }

    @Override
    protected CheckType getCheckType(Method method) {
        return method.getAnnotation(CallbackHandle.class).checkType();
    }

    public void dispatch(CallbackQuery query) {
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

        if (method == null) {
            log.warn("Unknown callback: {}", data);
            return;
        }

        String chatId = query.maybeInaccessibleMessage().chat().id().toString();
        String userId = query.from().id().toString();
        int messageId = query.inlineMessageId() != null ? Integer.parseInt(query.inlineMessageId()) : 0;

        String template = method.getAnnotation(CallbackHandle.class).value();
        Matcher matcher = patterns.containsKey(template) ? patterns.get(template).matcher(data) : null;

        Object[] args = buildArgs(method, query, chatId, userId, messageId, matcher, query);
        invokeWithCatch(method, beans.get(template), args, chatId);
    }
}
