package ru.tggc.botapp.domain.dto;

import lombok.Builder;

@Builder
public record CapybaraTeaDto(
        long userId,
        String name
) {
}
