package ru.tggc.capybaratelegrambot.registry.resolver;

import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.annotation.params.Ctx;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;

import java.lang.reflect.Parameter;

@Component
public class CtxResolver implements ParameterResolver<CapybaraContext> {

    @Override
    public boolean supports(Parameter p) {
        return p.isAnnotationPresent(Ctx.class);
    }

    @Override
    public CapybaraContext resolve(Parameter parameter, HandlerCtx ctx) {
        return new CapybaraContext(
                ctx.chatId(),
                ctx.from().id(),
                ctx.messageId()
        );
    }
}
