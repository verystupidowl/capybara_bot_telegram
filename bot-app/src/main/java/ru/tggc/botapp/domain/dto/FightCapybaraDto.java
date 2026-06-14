package ru.tggc.botapp.domain.dto;

import lombok.Builder;
import ru.tggc.botapp.domain.model.enums.fight.FightBuffEnum;

import java.util.List;

@Builder
public record FightCapybaraDto(boolean canFight, String fightTime, List<FightBuffEnum> buffs, Long chatId) {
}
