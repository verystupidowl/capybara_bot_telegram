package ru.tggc.capybaratelegrambot.domain.model.enums.fight;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.tggc.capybaratelegrambot.domain.fight.BossFightState;
import ru.tggc.capybaratelegrambot.domain.fight.effect.positive.AntiDebuffEffect;

import java.util.function.Consumer;

@Getter
@AllArgsConstructor
public enum FightBuffHeal implements FightBuffEnum {
    NONE("Ничего", "Ничего не делает", 0, stats -> {
    }),
    HEALING_HERB("🌿 Зелье травницы", "Увеличивает восстановление хп на 25%", 100,
            stats -> stats.setBaseHeal(stats.getBaseHeal() * 1.25)),
    ANTI_DEBUFFS("\uD83E\uDDEAПротивоядие", "Исцеление снимает отрицательные эффекты", 75,
            stats -> stats.getEffects().add(new AntiDebuffEffect())),
    ;


    private final String title;
    private final String description;
    private final int cost;
    private final Consumer<BossFightState.PlayerStats> effect;

    @Override
    public void apply(BossFightState.PlayerStats stats) {
        effect.accept(stats);
    }

    @Override
    public BuffType getBuffType() {
        return BuffType.HEAL;
    }
}
