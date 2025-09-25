package ru.tggc.capybaratelegrambot.domain.model.enums;

import lombok.Getter;

@Getter
public enum Type {
    FIRST("first", 0, 0);

    private final String label;
    private final Integer level;
    private final Integer gift;

    Type(final String label, Integer level, Integer gift) {
        this.label = label;
        this.level = level;
        this.gift = gift;
    }
}
