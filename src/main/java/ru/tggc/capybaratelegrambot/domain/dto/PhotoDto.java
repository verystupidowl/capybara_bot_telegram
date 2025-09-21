package ru.tggc.capybaratelegrambot.domain.dto;

import com.pengrad.telegrambot.model.request.Keyboard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public final class PhotoDto {
    private String url;
    private String caption;
    private String chatId;
    private Keyboard markup;
}
