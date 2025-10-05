package ru.tggc.capybaratelegrambot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.capybaratelegrambot.domain.dto.ChatDto;
import ru.tggc.capybaratelegrambot.domain.dto.UserDto;
import ru.tggc.capybaratelegrambot.domain.model.Chat;
import ru.tggc.capybaratelegrambot.domain.model.User;
import ru.tggc.capybaratelegrambot.domain.model.enums.UserRole;
import ru.tggc.capybaratelegrambot.exceptions.UserNotFoundException;
import ru.tggc.capybaratelegrambot.repository.ChatRepository;
import ru.tggc.capybaratelegrambot.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    @Transactional
    public void saveOrUpdate(UserDto dto, ChatDto chatDto) {
        Chat chat = chatRepository.findById(chatDto.id())
                .orElseGet(() -> chatRepository.save(Chat.builder()
                        .name(chatDto.title())
                        .id(chatDto.id())
                        .users(new HashSet<>())
                        .build()));
        User user = userRepository.findById(dto.userId())
                .orElseGet(() -> userRepository.save(User.builder()
                        .username(dto.username())
                        .id(dto.userId())
                        .createdAt(LocalDateTime.now())
                        .lastTimeUpdatedAt(LocalDateTime.now())
                        .userRole(UserRole.USER)
                        .chats(new HashSet<>())
                        .build()));
        user.getChats().add(chat);
        chat.getUsers().add(user);

        user.setLastTimeUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
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
