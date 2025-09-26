package ru.tggc.capybaratelegrambot.aop;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.CallbackHandle;
import ru.tggc.capybaratelegrambot.domain.dto.response.Response;
import ru.tggc.capybaratelegrambot.domain.model.enums.UserRole;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.service.UserService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class CallbackRegistry extends AbstractHandleRegistry<CallbackQuery> {

    protected CallbackRegistry(ListableBeanFactory beanFactory,
                               InlineKeyboardCreator inlineKeyboardCreator,
                               UserService userService) {
        super(beanFactory, inlineKeyboardCreator, userService);
    }

    @Override
    protected UserRole[] getRequiredRoles(Method method) {
        return method.getAnnotation(CallbackHandle.class).requiredRoles();
    }

    @Override
    protected Class<? extends Annotation> getHandleAnnotation() {
        return CallbackHandle.class;
    }

    public Response dispatch(CallbackQuery query) {
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
        Chat chat = query.maybeInaccessibleMessage().chat();
        User from = query.from();
        long chatId = chat.id();
        int messageId = query.maybeInaccessibleMessage().messageId();

        if (method == null) {
            log.warn("Unknown callback: {}", data);
            String message = buildMessageToAdmin("Unknown callback: " + data, chat, from);
            SendMessage sendMessageToUser = new SendMessage(chatId, NOT_IMPLEMENTED_MESSAGE);
            SendMessage sendMessageToAdmin = new SendMessage(ADMIN_ID, message);
            return Response.ofMessages(sendMessageToAdmin, sendMessageToUser);
        }


        String template = method.getAnnotation(CallbackHandle.class).value();
        Matcher matcher = patterns.containsKey(template) ? patterns.get(template).matcher(data) : null;

        Object[] args = buildArgs(method, query, chatId, from.id(), messageId, matcher, query);
        return invokeWithCatch(from, method, beans.get(template), args, chat);
    }
}
