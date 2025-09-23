package ru.tggc.capybaratelegrambot.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.capybaratelegrambot.domain.dto.UserDto;
import ru.tggc.capybaratelegrambot.domain.model.User;
import ru.tggc.capybaratelegrambot.exceptions.UserNotFoundException;
import ru.tggc.capybaratelegrambot.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getUserByUserId(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User with userId " + userId + " not found"));
    }

    @Transactional
    public User saveOrUpdate(UserDto dto) {
        User user = userRepository.findByUserId(dto.userId())
                .orElseGet(() -> User.builder()
                        .username(dto.username())
                        .userId(dto.userId())
                        .createdAt(LocalDateTime.now())
                        .lastTimeUpdatedAt(LocalDateTime.now())
                        .build());
        user.setLastTimeUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
