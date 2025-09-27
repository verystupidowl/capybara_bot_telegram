package ru.tggc.capybaratelegrambot.aop.annotation.handle;

import ru.tggc.capybaratelegrambot.domain.model.enums.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PhotoHandle {

    String value();

    UserRole[] requiredRoles() default {};
}
