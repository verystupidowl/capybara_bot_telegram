package ru.tggc.capybaratelegrambot.aop.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Deprecated
public @interface CheckCapybara {
    CheckType checkType() default CheckType.CHECK_NOT_EXISTS;
    boolean needSearch() default false;
}
