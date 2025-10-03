package ru.tggc.capybaratelegrambot.provider;

import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.domain.dto.BossFightState;
import ru.tggc.capybaratelegrambot.domain.dto.UserDto;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BossFightProvider {
    private final Map<Long, BossFightState> currentFights = new ConcurrentHashMap<>();
    private final Map<Long, Set<UserDto>> preparingFights = new ConcurrentHashMap<>();

    public boolean canStartFight(Long chatId) {
        return preparingFights.get(chatId) != null && !preparingFights.get(chatId).isEmpty();
    }

    public void joinFight(Long chatId, Long userId, String username) {
        UserDto userDto = new UserDto(userId, username);
        Set<UserDto> users = preparingFights.getOrDefault(chatId, new HashSet<>());
        users.add(userDto);
        preparingFights.put(chatId, users);
    }

    public Set<UserDto> getPreparedUsers(Long chatId) {
        return preparingFights.get(chatId);
    }

    public Set<UserDto> popPreparedUsers(Long chatId) {
        Set<UserDto> users = preparingFights.get(chatId);
        preparingFights.remove(chatId);
        return users;
    }

    public void leaveFight(Long chatId, Long userId) {
        UserDto userDto = new UserDto(userId, null);
        Set<UserDto> users = preparingFights.getOrDefault(chatId, new HashSet<>());
        users.remove(userDto);
        preparingFights.put(chatId, users);
    }

    public void startFight(Long chatId, BossFightState fight) {
        currentFights.put(chatId, fight);
    }

    public Optional<BossFightState> getFight(Long chatId) {
        return Optional.ofNullable(currentFights.get(chatId));
    }

    public void endFight(Long chatId) {
        currentFights.remove(chatId);
    }
}
