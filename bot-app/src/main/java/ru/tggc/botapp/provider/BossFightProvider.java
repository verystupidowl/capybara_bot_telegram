package ru.tggc.botapp.provider;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;
import ru.tggc.botapp.domain.dto.fight.BossFightState;
import ru.tggc.telegrambotframework.dto.UserDto;

import java.time.Duration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BossFightProvider {
    private final Cache<Long, BossFightState> currentFights = Caffeine.newBuilder()
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
        Set<UserDto> users = preparingFights.get(chatId, _ -> new HashSet<>());
        Objects.requireNonNull(users).add(userDto);
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
        Set<UserDto> users = preparingFights.get(chatId, _ -> new HashSet<>());
        Objects.requireNonNull(users).removeIf(u -> u.userId().equals(userId));
        preparingFights.put(chatId, users);
    }

    public void startFight(Long chatId, BossFightState fight) {
        currentFights.put(chatId, fight);
    }

    public Optional<BossFightState> getFight(Long chatId) {
        return Optional.ofNullable(currentFights.getIfPresent(chatId));
    }

    public void endFight(Long chatId) {
        currentFights.invalidate(chatId);
    }
}
