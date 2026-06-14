package ru.tggc.botapp.domain.model.enums.fight;

import ru.tggc.botapp.domain.dto.fight.BossFightState;

import java.util.function.Consumer;

public interface BuffEffect extends Consumer<BossFightState.PlayerStats> {

    static BuffEffect empty() {
        return _ -> {
        };
    }
}
