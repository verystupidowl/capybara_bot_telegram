package ru.tggc.telegrambotframework.dto;

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
