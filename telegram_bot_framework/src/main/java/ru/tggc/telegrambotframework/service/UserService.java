package ru.tggc.telegrambotframework.service;

import ru.tggc.telegrambotframework.dto.UserRole;

public interface UserService {

    boolean checkRoles(Long id, UserRole[] requiredRoles);
}
