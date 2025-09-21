package ru.tggc.capybaratelegrambot.domain.dto;

import lombok.Getter;

@Getter
public enum HappinessThings {
    STROKE("Ты погладил капибару!\nСчастье увеличилось на 5", 5),
    FEED("Ты покормил капибару!\nСчастье увеличилось на 20!", 20),
    WALK("Ты погулял с капибарой!\nСчастье увеличилось на 15", 15),
    PLAY("Ты поиграл с капибарой!\nСчастье увеличилось на 10", 10),
    SCOLD("Ты поругал капибару\nСчастье уменьшилось на 10", -10);

    private final String label;
    private final Integer level;

    HappinessThings(String label, Integer level) {
        this.label = label;
        this.level = level;
    }
}
