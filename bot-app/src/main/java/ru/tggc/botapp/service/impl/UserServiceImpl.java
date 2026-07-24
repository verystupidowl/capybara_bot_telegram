package ru.tggc.botapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.botapp.domain.dto.BlockInfoDto;
import ru.tggc.botapp.domain.model.Chat;
import ru.tggc.botapp.domain.model.User;
import ru.tggc.botapp.exceptions.UserNotFoundException;
import ru.tggc.botapp.repository.BlockRepository;
import ru.tggc.botapp.repository.ChatRepository;
import ru.tggc.botapp.repository.UserRepository;
import ru.tggc.telegrambotcore.dto.ChatDto;
import ru.tggc.telegrambotcore.dto.UserDto;
import ru.tggc.telegrambotcore.dto.UserRole;
import ru.tggc.telegrambotcore.service.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final BlockRepository blockRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveOrUpdate(@NotNull UserDto userDto, @NotNull ChatDto chatDto) {
        Chat chat = chatRepository.findById(chatDto.id())
                .orElseGet(() -> chatRepository.save(Chat.builder()
                        .name(chatDto.title())
                        .id(chatDto.id())
                        .users(new HashSet<>())
                        .build()));
        User user = userRepository.findById(userDto.userId())
                .orElseGet(() -> userRepository.save(User.builder()
                        .username(userDto.username())
                        .id(userDto.userId())
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

    @Override
    public boolean checkRoles(long id, @NotNull UserRole[] requiredRoles) {
        return userRepository.findById(id).stream()
                .anyMatch(u -> Arrays.stream(requiredRoles).toList().contains(u.getUserRole()));
    }

    @Transactional(readOnly = true)
    public Optional<BlockInfoDto> getBlockReason(String username) {
        return blockRepository.findByUserUsername(username)
                .map(bi -> new BlockInfoDto(
                        bi.getReason(),
                        bi.getUser().getUsername(),
                        bi.getReporter().getUsername()
                ));
    }
}
