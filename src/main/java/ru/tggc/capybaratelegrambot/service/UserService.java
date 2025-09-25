package ru.tggc.capybaratelegrambot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.capybaratelegrambot.domain.dto.UserDto;
import ru.tggc.capybaratelegrambot.domain.model.User;
import ru.tggc.capybaratelegrambot.domain.model.enums.UserRole;
import ru.tggc.capybaratelegrambot.exceptions.UserNotFoundException;
import ru.tggc.capybaratelegrambot.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User saveOrUpdate(UserDto dto) {
        User user = userRepository.findById(Long.parseLong(dto.userId()))
                .orElseGet(() -> User.builder()
                        .username(dto.username())
                        .id(Long.parseLong(dto.userId()))
                        .createdAt(LocalDateTime.now())
                        .lastTimeUpdatedAt(LocalDateTime.now())
                        .userRole(UserRole.USER)
                        .build());
        user.setLastTimeUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with userId " + id + " not found"));
    }

    public boolean checkRoles(Long id, UserRole[] requiredRoles) {
        return userRepository.findById(id).stream()
                .anyMatch(u -> Arrays.stream(requiredRoles).toList().contains(u.getUserRole()));
    }
}
