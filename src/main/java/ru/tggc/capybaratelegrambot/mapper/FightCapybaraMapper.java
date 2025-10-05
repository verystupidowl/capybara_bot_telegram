package ru.tggc.capybaratelegrambot.mapper;

import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.domain.dto.FightCapybaraDto;
import ru.tggc.capybaratelegrambot.domain.model.Fight;
import ru.tggc.capybaratelegrambot.utils.Utils;

import java.util.List;

@Component
public class FightCapybaraMapper {

    public FightCapybaraDto toDto(Fight fight) {
        return FightCapybaraDto.builder()
                .canFight(fight.getFightAction().canPerform())
                .fightTime(Utils.formatDuration(fight.getFightAction().timeUntilNext()))
                .buffs(List.of(
                        fight.getWeapon(),
                        fight.getShield(),
                        fight.getHeal(),
                        fight.getSpecial()
                ))
                .build();
    }
}
