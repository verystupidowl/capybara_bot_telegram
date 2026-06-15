package ru.tggc.botapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.botapp.domain.dto.BlockInfoDto;
import ru.tggc.botapp.domain.model.BlockInfo;
import ru.tggc.botapp.domain.model.Chat;
import ru.tggc.botapp.domain.model.User;
import ru.tggc.botapp.exceptions.UserNotFoundException;
import ru.tggc.botapp.repository.BlockRepository;
import ru.tggc.botapp.repository.ChatRepository;
import ru.tggc.botapp.repository.UserRepository;
import ru.tggc.telegrambotframework.dto.ChatDto;
import ru.tggc.telegrambotframework.dto.UserDto;
import ru.tggc.telegrambotframework.dto.UserRole;
import ru.tggc.telegrambotframework.service.UserService;

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

    @Transactional
    public User saveOrUpdate(UserDto dto, ChatDto chatDto) {
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

        return userRepository.save(user);
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
    public boolean checkRoles(Long id, UserRole[] requiredRoles) {
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void blockUser(String username, String reason, String reporterUsername) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with username " + username + " not found"));
        User reporter = userRepository.findByUsername(reporterUsername)
                .orElseThrow(() -> new UserNotFoundException("User with reporter username " + reporterUsername + " not found"));

        BlockInfo blockInfo = new BlockInfo();
        blockInfo.setReason(reason);
        blockInfo.setUser(user);
        blockInfo.setReporter(reporter);
        blockRepository.save(blockInfo);
    }
}
