package ru.tggc.telegrambotframework.dto;

import lombok.Builder;

@Builder
public record ChatDto(Long id, String title) {
}
