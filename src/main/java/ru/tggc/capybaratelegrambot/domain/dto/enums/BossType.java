package ru.tggc.capybaratelegrambot.domain.dto.enums;

import lombok.Getter;

import java.util.List;

@Getter
public enum BossType {
    SMALL_CROCODILE(
            "Хилый крокодил",
            500,
            BossAction.TAIL_ON_THE_WATER,
            BossAction.BITE,
            BossAction.STUN,
            BossAction.AOE_DAMAGE,
            BossAction.AOE_STUN,
            BossAction.FOCUSED_STRIKE,
            BossAction.HEAL
    );

    private final String name;
    private final int hp;
    private final List<BossAction> bossActions;

    BossType(String name, int hp, BossAction... bossActions) {
        this.name = name;
        this.hp = hp;
        this.bossActions = List.of(bossActions);
    }
}
