package ru.tggc.capybaratelegrambot.domain.dto;

import lombok.Builder;

@Builder
public record CapybaraTeaDto(
        String userId,
        String name
) {
}
