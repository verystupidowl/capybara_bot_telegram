package ru.tggc.telegrambotframework.registry.resolver;

import org.springframework.stereotype.Component;
import ru.tggc.telegrambotframework.annotation.params.ChatId;

import java.lang.reflect.Parameter;

@Component
public class ChatIdResolver implements ParameterResolver<Long> {

    @Override
    public boolean supports(Parameter p) {
        return p.isAnnotationPresent(ChatId.class);
    }

    @Override
    public Long resolve(Parameter parameter, HandlerCtx ctx) {
        return ctx.chat().id();
    }
}
