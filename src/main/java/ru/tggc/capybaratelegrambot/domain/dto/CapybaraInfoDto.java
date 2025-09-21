package ru.tggc.capybaratelegrambot.domain.dto;

import lombok.Builder;

@Builder
public record CapybaraInfoDto(
        String name,
        Boolean canTea,
        Boolean isTeaWaiting,
        Boolean canHappiness,
        Integer happinessTime,
        long teaTime,
        Boolean hasWork,
        Boolean canGoWork,
        Boolean canGoBigJob,
        long workTime,
        Boolean isWorking,
        Boolean isOnBigJob,
        Boolean canTakeFromWork,
        Integer takeFromWork,
        Integer rise,
        Integer index,
        Boolean canTakeFromBigJob,
        Integer takeFromBigJob,
        Integer level,
        Integer bigJobTime,
        Boolean canSatiety,
        Integer satietyTime,
        Boolean canRace,
        Integer raceTime,
        String improvement
) {
}
