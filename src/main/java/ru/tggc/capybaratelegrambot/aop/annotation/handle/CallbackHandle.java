package ru.tggc.capybaratelegrambot.aop.annotation.handle;

import ru.tggc.capybaratelegrambot.aop.annotation.CheckType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CallbackHandle {

    String value();

    CheckType checkType() default CheckType.CHECK_NOT_EXISTS;

    boolean needSearch() default false;
}
