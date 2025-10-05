package ru.tggc.capybaratelegrambot.domain.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tggc.capybaratelegrambot.domain.model.enums.fight.FightBuffHeal;
import ru.tggc.capybaratelegrambot.domain.model.enums.fight.FightBuffShield;
import ru.tggc.capybaratelegrambot.domain.model.enums.fight.FightBuffSpecial;
import ru.tggc.capybaratelegrambot.domain.model.enums.fight.FightBuffWeapon;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.FightAction;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private FightAction fightAction;
    private int wins;
    private int loses;
    @Enumerated(EnumType.STRING)
    private FightBuffWeapon weapon;
    @Enumerated(EnumType.STRING)
    private FightBuffShield shield;
    @Enumerated(EnumType.STRING)
    private FightBuffHeal heal;
    @Enumerated(EnumType.STRING)
    private FightBuffSpecial special;
}
