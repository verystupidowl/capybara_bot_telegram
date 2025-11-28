package ru.tggc.capybaratelegrambot.domain.fight;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DamageEvent {
    private double damage;
}
