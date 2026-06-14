package ru.tggc.botapp.domain.model.enums.fight;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.tggc.botapp.domain.dto.fight.BossFightState;
import ru.tggc.botapp.domain.dto.fight.effect.positive.AegisEffect;

@AllArgsConstructor
@Getter
public enum FightBuffSpecial implements FightBuffEnum {
    NONE("none", "none", 0, BuffEffect.empty()),
    AEGIS("Аегис", "В разработке. Пока ничего не дает", 500, stats ->
            stats.getEffects().add(new AegisEffect()));

    private final String title;
    private final String description;
    private final int cost;
    private final BuffEffect effect;

    @Override
    public void apply(BossFightState.PlayerStats player) {
        effect.accept(player);
    }

    @Override
    public BuffType getBuffType() {
        return BuffType.SPECIAL;
    }
}
