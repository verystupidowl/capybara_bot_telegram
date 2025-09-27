package ru.tggc.capybaratelegrambot.domain.dto.enums;

import lombok.Getter;

@Getter
public enum HappinessThings {
    STROKE("Ты погладил капибару!\nСчастье увеличилось на 5", 5, "https://vk.com/photo-209917797_457246188"),
    FEED("Ты покормил капибару!\nСчастье увеличилось на 20!", 20, "https://vk.com/photo-209917797_457246189"),
    WALK("Ты погулял с капибарой!\nСчастье увеличилось на 15", 15, "https://vk.com/photo-209917797_457246190"),
    PLAY("Ты поиграл с капибарой!\nСчастье увеличилось на 10", 10, "https://vk.com/photo-209917797_457246191"),
    SCOLD("Ты поругал капибару\nСчастье уменьшилось на 10", -10, "https://vk.com/photo-209917797_457246192");

    private final String label;
    private final Integer level;
    private final String photoUrl;

    HappinessThings(String label, Integer level, String photoUrl) {
        this.label = label;
        this.level = level;
        this.photoUrl = photoUrl;
    }
}
