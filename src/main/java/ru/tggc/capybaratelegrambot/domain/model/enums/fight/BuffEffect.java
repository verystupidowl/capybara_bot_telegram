package ru.tggc.capybaratelegrambot.domain.model.enums.fight;

import ru.tggc.capybaratelegrambot.domain.fight.BossFightState;

import java.util.function.Consumer;

public interface BuffEffect extends Consumer<BossFightState.PlayerStats> {

    static BuffEffect empty() {
        return ps -> {
        };
    }
}
