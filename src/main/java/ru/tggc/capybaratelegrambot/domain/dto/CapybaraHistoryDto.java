package ru.tggc.capybaratelegrambot.domain.dto;

import lombok.Builder;

@Builder
public record CapybaraHistoryDto(
        String chatId,
        String userId
) {
}
