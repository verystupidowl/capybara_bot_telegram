package ru.tggc.capybaratelegrambot.domain.dto;

import lombok.Builder;

import java.util.Objects;

@Builder
public record CapybaraContext(
        long chatId,
        long userId,
        int messageId
) {

    @Override
    public int hashCode() {
        return Objects.hash(chatId, userId);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CapybaraContext that = (CapybaraContext) o;
        return chatId == that.chatId && userId == that.userId;
    }
}
