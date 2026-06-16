package ru.tggc.telegrambotframework.access.annotationprovider;

import ru.tggc.telegrambotframework.annotation.handle.HandleMeta;

import java.lang.reflect.Method;

public interface AnnotationProvider {

    boolean supports(Method m);

    HandleMeta extractMeta(Method m);
}
