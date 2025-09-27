package ru.tggc.capybaratelegrambot.domain.dto.enums;

import lombok.Getter;

@Getter
public enum FileType {
    PHOTO("photo"),
    DOC("doc");

    private final String label;

    FileType(String label) {
        this.label = label;
    }
}
