package ru.tggc.botapp.mapper;

import org.springframework.stereotype.Component;
import ru.tggc.botapp.domain.dto.FightCapybaraDto;
import ru.tggc.botapp.domain.model.Fight;
import ru.tggc.telegrambotframework.util.Utils;

import java.util.List;

@Component
public class FightCapybaraMapper {

    public FightCapybaraDto toDto(Fight fight, long chatId) {
        return FightCapybaraDto.builder()
                .canFight(fight.getFightAction().canPerform())
                .fightTime(Utils.formatDuration(fight.getFightAction().timeUntilNext()))
                .buffs(List.of(
                        fight.getWeapon(),
                        fight.getShield(),
                        fight.getHeal(),
                        fight.getSpecial()
                ))
                .chatId(chatId)
                .build();
    }
}
