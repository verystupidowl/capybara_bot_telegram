package ru.tggc.capybaratelegrambot.domain.dto;

import lombok.Getter;

@Getter
public enum CapybaraDefaultPhoto {
    ;
    private final String url;

    CapybaraDefaultPhoto(String url) {
        this.url = url;
    }
}
