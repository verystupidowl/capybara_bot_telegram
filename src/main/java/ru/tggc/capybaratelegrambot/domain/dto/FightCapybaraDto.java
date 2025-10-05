package ru.tggc.capybaratelegrambot.domain.dto;

import lombok.Builder;
import ru.tggc.capybaratelegrambot.domain.model.enums.fight.FightBuffEnum;

import java.util.List;

@Builder
public record FightCapybaraDto(boolean canFight, String fightTime, List<FightBuffEnum> buffs) {
}
