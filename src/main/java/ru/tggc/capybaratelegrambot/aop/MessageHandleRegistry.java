package ru.tggc.capybaratelegrambot.aop;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.MessageHandle;
import ru.tggc.capybaratelegrambot.domain.dto.response.Response;
import ru.tggc.capybaratelegrambot.domain.model.enums.UserRole;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.service.UserService;
import ru.tggc.capybaratelegrambot.utils.Utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class MessageHandleRegistry extends AbstractHandleRegistry<Message> {

    protected MessageHandleRegistry(ListableBeanFactory beanFactory,
                                    InlineKeyboardCreator inlineKeyboardCreator,
                                    UserService userService) {
        super(beanFactory, inlineKeyboardCreator, userService);
    }

    @Override
    protected UserRole[] getRequiredRoles(Method method) {
        return Utils.getOr(
                method.getAnnotation(MessageHandle.class),
                MessageHandle::requiredRoles,
                new UserRole[0]
        );
    }

    @Override
    protected Class<? extends Annotation> getHandleAnnotation() {
        return MessageHandle.class;
    }

    public Response dispatch(Message message) {
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

        Chat chat = message.chat();
        User from = message.from();
        Response response = null;

        if (method == null) {
            if (defaultMethod == null) {
                log.warn("Unknown message: {}", text);
            } else {
                Object[] args = buildArgs(defaultMethod, message, chat.id(), from.id(), 0, null, message);
                response = invokeWithCatch(from, defaultMethod, defaultBean, args, chat);
            }
            return response;
        }


        String template = method.getAnnotation(MessageHandle.class).value();
        Matcher matcher = patterns.containsKey(template) ? patterns.get(template).matcher(text) : null;

        Object[] args = buildArgs(method, message, chat.id(), from.id(), 0, matcher, message);
        return invokeWithCatch(from, method, beans.get(template), args, chat);
    }
}
