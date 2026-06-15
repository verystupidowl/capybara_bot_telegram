package ru.tggc.telegrambotframework.access.annotationprovider;

import org.springframework.stereotype.Component;
import ru.tggc.telegrambotframework.annotation.handle.HandleMeta;
import ru.tggc.telegrambotframework.annotation.handle.PhotoHandle;

import java.lang.reflect.Method;

@Component
public class PhotoAnnotationProvider implements AnnotationProvider {

    @Override
    public boolean supports(Method m) {
        return m.isAnnotationPresent(PhotoHandle.class);
    }

    @Override
    public HandleMeta extractMeta(Method m) {
        return HandleMeta.from(m.getAnnotation(PhotoHandle.class));
    }
}
