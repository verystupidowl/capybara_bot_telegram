package ru.tggc.botapp.formatter;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import ru.tggc.botapp.domain.dto.fight.enums.BossType;
import ru.tggc.telegrambotframework.dto.UserDto;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BossFightFormatter {
    private final MessageSource messageSource;

    public String getStartMessage(BossType bossType) {
        return getMsg("fight.start-message", bossType.getName(), bossType.getHp());
    }

    public String getUsersMessage(Set<UserDto> users) {
        String usernames = users.stream()
                .map(UserDto::username)
                .collect(Collectors.joining("\n"));
        return getMsg("fight.preparing-users", users.size(), usernames);
    }

    public String getCantActMessage() {
        return getMsg("fight.cant-act");
    }

    public String getPlayerChoseMessage(String caption, String username, String action) {
        return getMsg("fight.player-chose", caption, username, action);
    }

    public String getMsg(String key, Object... args) {
        return messageSource.getMessage(key, args, null);
    }
}
