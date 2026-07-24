package ru.tggc.botapp.domain.dto;

import lombok.Builder;

@Builder
public record HappinessThingDto(
        String title,
        Integer level,
        String photoUrl
) {
}
