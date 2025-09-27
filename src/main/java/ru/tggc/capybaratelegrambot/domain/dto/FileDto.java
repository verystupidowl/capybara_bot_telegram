package ru.tggc.capybaratelegrambot.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tggc.capybaratelegrambot.domain.dto.enums.FileType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileDto {
    private String url;
    private FileType type;
}
