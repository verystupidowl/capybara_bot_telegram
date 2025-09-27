package ru.tggc.capybaratelegrambot.domain.dto;

import lombok.Builder;

@Builder
public record CapybaraContext(
        long chatId,
        long userId
) {
}
