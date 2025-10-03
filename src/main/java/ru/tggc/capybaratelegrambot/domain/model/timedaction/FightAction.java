package ru.tggc.capybaratelegrambot.domain.model.timedaction;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Embeddable
@NoArgsConstructor
public class FightAction implements TimedAction {
    private LocalDateTime lastFight;
    private Duration cooldown;

    public FightAction(Duration cooldown) {
        this.cooldown = cooldown;
    }

    @Override
    public boolean canPerform() {
        return lastFight == null || Duration.between(lastFight, LocalDateTime.now()).compareTo(cooldown) >= 0;
    }

    @Override
    public Duration timeUntilNext() {
        if (lastFight == null) return Duration.ZERO;
        Duration passed = Duration.between(lastFight, LocalDateTime.now());
        return passed.compareTo(cooldown) >= 0 ? Duration.ZERO : cooldown.minus(passed);
    }
}
