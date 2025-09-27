package ru.tggc.capybaratelegrambot.domain.dto;

import lombok.Builder;

@Builder
public record MyCapybaraDto(
        String name,
        Integer wins,
        Integer defeats,
        Integer level,
        String type,
        Integer cheerfulness,
        String job,
        Long currency,
        String wedding,
        Integer satietyLevel,
        Integer satietyMaxLevel,
        Integer happinessLevel,
        Integer happinessMaxLevel,
        Boolean canGoWork,
        Boolean canTakeFromWork,
        Boolean canSatiety,
        Boolean canHappy,
        Boolean hasWork,
        String photo
) {
}
