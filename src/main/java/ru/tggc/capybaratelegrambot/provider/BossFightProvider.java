package ru.tggc.capybaratelegrambot.provider;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.domain.dto.UserDto;
import ru.tggc.capybaratelegrambot.domain.sm.BossFightEvents;
import ru.tggc.capybaratelegrambot.domain.sm.BossFightStates;

import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BossFightProvider {
    private final Cache<Long, StateMachine<BossFightStates, BossFightEvents>> currentFights = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(1))
            .maximumSize(10_000)
            .build();
    private final Cache<Long, Set<UserDto>> preparingFights = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(1))
            .maximumSize(10_000)
            .build();

    public boolean canStartFight(Long chatId) {
        Set<UserDto> users = preparingFights.getIfPresent(chatId);
        return users != null && !users.isEmpty();
    }

    public String joinFight(Long chatId, Long userId, String username) {
        UserDto userDto = new UserDto(userId, username);
        Set<UserDto> users = preparingFights.get(chatId, id -> new HashSet<>());
        users.add(userDto);
        preparingFights.put(chatId, users);

        return "Состав боя:\n" + users.stream()
                .map(u -> "@" + u.username())
                .collect(Collectors.joining("\n"));
    }

    public Set<UserDto> getPreparedUsers(Long chatId) {
        return preparingFights.getIfPresent(chatId);
    }

    public Set<UserDto> popPreparedUsers(Long chatId) {
        Set<UserDto> users = preparingFights.getIfPresent(chatId);
        preparingFights.invalidate(chatId);
        return users != null ? users : new HashSet<>();
    }

    public void leaveFight(Long chatId, Long userId) {
        Set<UserDto> users = preparingFights.get(chatId, id -> new HashSet<>());
        users.removeIf(u -> u.userId().equals(userId));
        preparingFights.put(chatId, users);
    }

    public void startFight(Long chatId, StateMachine<BossFightStates, BossFightEvents> fight) {
        currentFights.put(chatId, fight);
    }

    public Optional<StateMachine<BossFightStates, BossFightEvents>> getFight(Long chatId) {
        return Optional.ofNullable(currentFights.getIfPresent(chatId));
    }

    public void endFight(Long chatId) {
        currentFights.invalidate(chatId);
    }
}
