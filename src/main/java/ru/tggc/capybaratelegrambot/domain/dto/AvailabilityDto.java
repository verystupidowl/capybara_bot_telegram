package ru.tggc.capybaratelegrambot.domain.dto;

import lombok.Builder;

@Builder
public record AvailabilityDto(
        Boolean isWorking,
        Boolean isOnBigJob,
        Boolean canTakeFromWork,
        Boolean hasWork,
        Boolean canGoWork,
        Boolean canGoBigJob,
        Boolean canTea,
        Boolean isTeaWaiting,
        Boolean canHappiness
) {
}
