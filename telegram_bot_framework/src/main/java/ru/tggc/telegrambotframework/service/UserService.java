package ru.tggc.telegrambotframework.service;

import ru.tggc.telegrambotframework.dto.ChatDto;
import ru.tggc.telegrambotframework.dto.UserDto;
import ru.tggc.telegrambotframework.dto.UserRole;

public interface UserService {

    void saveOrUpdate(UserDto userDto, ChatDto chatDto);

    boolean checkRoles(Long id, UserRole[] requiredRoles);
}
