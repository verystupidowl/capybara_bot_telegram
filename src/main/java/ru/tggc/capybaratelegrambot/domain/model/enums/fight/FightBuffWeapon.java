package ru.tggc.capybaratelegrambot.domain.model.enums.fight;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.tggc.capybaratelegrambot.domain.fight.BossFightState;
import ru.tggc.capybaratelegrambot.domain.fight.effect.positive.VampirismEffect;

import java.util.function.Consumer;

@Getter
@AllArgsConstructor
public enum FightBuffWeapon implements FightBuffEnum {
    NONE("none", "none", 0, stats -> {
    }),
    FIRE_CARROT("Огненная морковка🥕🔥", "Атаки наносит на 25% больше урона", 100,
            stats -> stats.setBaseDamage(stats.getBaseDamage() * 1.25)),
    DAGGER("Кровавый кинжал \uD83E\uDE78", "Атаки восстанавливают 50% от нанесенного урона. Урон снижен на 10%", 250,
            stats -> {
                stats.setBaseDamage(stats.getBaseDamage() * 0.9);
                stats.getEffects().add(new VampirismEffect(0.5));
            }),
    CAPYBARA_SWORD("Меч капибары-рыцаря ⚔️", "Шанс крита увеличен на 20%", 200,
            stats -> stats.setCritChance(stats.getCritChance() + 20));

    private final String title;
    private final String description;
    private final int cost;
    private final Consumer<BossFightState.PlayerStats> effect;

    @Override
    public void apply(BossFightState.PlayerStats player) {
        effect.accept(player);
    }

    @Override
    public BuffType getBuffType() {
        return BuffType.ATTACK;
    }
}
