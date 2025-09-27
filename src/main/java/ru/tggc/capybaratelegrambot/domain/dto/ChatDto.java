package ru.tggc.capybaratelegrambot.domain.dto;

import lombok.Builder;

@Builder
public record ChatDto(Long id, String title) {
}
