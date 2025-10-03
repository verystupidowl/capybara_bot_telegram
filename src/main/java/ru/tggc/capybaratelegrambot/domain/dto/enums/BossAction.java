package ru.tggc.capybaratelegrambot.domain.dto.enums;

import lombok.Getter;
import ru.tggc.capybaratelegrambot.utils.RandomUtils;

@Getter
public enum BossAction {
    TAIL_ON_THE_WATER(RandomUtils.getRandomInt(30) + 10),
    BITE(RandomUtils.getRandomInt(30) + 50),
    STUN(RandomUtils.getRandomInt(30)),
    AOE_DAMAGE(RandomUtils.getRandomInt(10) + 10),
    AOE_STUN(RandomUtils.getRandomInt(10)),
    HEAL(RandomUtils.getRandomInt(30)),
    FOCUSED_STRIKE(RandomUtils.getRandomInt(30) + 20);

    private final int damage;

    BossAction(int damage) {
        this.damage = damage;
    }
}
