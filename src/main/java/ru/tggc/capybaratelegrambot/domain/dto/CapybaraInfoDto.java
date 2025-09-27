package ru.tggc.capybaratelegrambot.domain.dto;

import lombok.Builder;

@Builder
public record CapybaraInfoDto(
        String name,
        Boolean canTea,
        Boolean isTeaWaiting,
        Boolean canHappiness,
        String happinessTime,
        String teaTime,
        Boolean hasWork,
        Boolean canGoWork,
        Boolean canGoBigJob,
        String workTime,
        Boolean isWorking,
        Boolean isOnBigJob,
        Boolean canTakeFromWork,
        String takeFromWork,
        Integer rise,
        Integer index,
        Boolean canTakeFromBigJob,
        Integer takeFromBigJob,
        Integer level,
        Integer bigJobTime,
        Boolean canSatiety,
        String satietyTime,
        Boolean canRace,
        String raceTime,
        String improvement
) {
}
