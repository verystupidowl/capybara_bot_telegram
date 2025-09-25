package ru.tggc.capybaratelegrambot.domain.dto;

import lombok.Builder;

@Builder
public record CapybaraContext(
        String chatId,
        String userId
) {
}
