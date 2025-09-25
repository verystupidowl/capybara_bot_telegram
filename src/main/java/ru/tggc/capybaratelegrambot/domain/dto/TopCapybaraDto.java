package ru.tggc.capybaratelegrambot.domain.dto;

import lombok.Builder;

@Builder
public record TopCapybaraDto(String name, PhotoDto photoDto) {
}
