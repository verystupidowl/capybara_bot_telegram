package ru.tggc.botapp.domain.dto;

import lombok.Builder;
import ru.tggc.telegrambotcore.dto.PhotoDto;

@Builder
public record TopCapybaraDto(String name, PhotoDto photoDto, int level) {
}
