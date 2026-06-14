package ru.tggc.capybaratelegrambot.registry.resolver;

import java.lang.reflect.Parameter;

public interface ParameterResolver<P> {

    boolean supports(Parameter p);

    P resolve(Parameter parameter, HandlerCtx ctx);
}
