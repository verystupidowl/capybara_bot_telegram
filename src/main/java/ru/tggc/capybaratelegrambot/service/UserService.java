package ru.tggc.capybaratelegrambot.service;

import ru.tggc.capybaratelegrambot.domain.dto.UserDto;
import ru.tggc.capybaratelegrambot.domain.model.User;

public interface UserService {
    User getUserByUserId(String userId);
    User saveOrUpdate(UserDto user);
    User getUserByUsername(String username);
}
