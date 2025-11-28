package ru.tggc.capybaratelegrambot.domain.fight.enums;

import lombok.Getter;

import java.util.List;

@Getter
public enum BossType {
    SMALL_CROCODILE(
            "🐊Хилый крокодил",
            500,
            200,
            BossAction.TAIL_ON_THE_WATER,
            BossAction.BITE
    ),
    MEDIUM_CROCODILE(
            "🐊Средний крокодил",
            750,
            300,
            BossAction.TAIL_ON_THE_WATER,
            BossAction.BITE,
            BossAction.STUN
    ),
    EVIL_CROCODILE(
            "🐊Злой крокодил",
            1000,
            500,
            BossAction.TAIL_ON_THE_WATER,
            BossAction.BITE,
            BossAction.STUN,
            BossAction.AOE_STUN,
            BossAction.AOE_DAMAGE,
            BossAction.FOCUSED_STRIKE,
            BossAction.HEAL,
            BossAction.TAIL_MUD_SPLASH
    ),
    ANACONDA(
            "Анаконда \uD83D\uDC0D",
            700,
            500,
            BossAction.BITE,
            BossAction.POISON_BITE,
            BossAction.TAIL_SLAM_DUST
    ),
    ;


    private final String name;
    private final int hp;
    private final int cost;
    private final List<BossAction> bossActions;

    BossType(String name, int hp, int cost, BossAction... bossActions) {
        this.name = name;
        this.hp = hp;
        this.cost = cost;
        this.bossActions = List.of(bossActions);
    }
}
