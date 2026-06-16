package ru.tggc.telegrambotframework.access.annotationprovider;

import org.springframework.stereotype.Component;
import ru.tggc.telegrambotframework.annotation.handle.CommandHandle;
import ru.tggc.telegrambotframework.annotation.handle.HandleMeta;

import java.lang.reflect.Method;

@Component
public class CommandAnnotationProvider implements AnnotationProvider {

    @Override
    public boolean supports(Method m) {
        return m.isAnnotationPresent(CommandHandle.class);
    }

    @Override
    public HandleMeta extractMeta(Method m) {
        return HandleMeta.from(m.getAnnotation(CommandHandle.class));
    }
}
