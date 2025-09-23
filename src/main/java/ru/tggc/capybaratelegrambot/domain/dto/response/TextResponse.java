package ru.tggc.capybaratelegrambot.domain.dto.response;

import lombok.Data;
import ru.tggc.capybaratelegrambot.domain.dto.ResponseType;

@Data
public class TextResponse implements Response {
    private String chatId;

    @Override
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.TEXT_MESSAGE;
    }
}
