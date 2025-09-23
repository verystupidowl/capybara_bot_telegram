package ru.tggc.capybaratelegrambot.domain.model.timedaction;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Happiness implements TimedAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private Integer level;
    private Integer maxLevel;

    private LocalDateTime lastHappy;

    private static final Duration COOLDOWN = Duration.ofHours(2);

    @Override
    public boolean canPerform() {
        return lastHappy == null || Duration.between(lastHappy, LocalDateTime.now()).compareTo(COOLDOWN) >= 0;
    }

    @Override
    public Duration timeUntilNext() {
        if (lastHappy == null) return Duration.ZERO;
        Duration passed = Duration.between(lastHappy, LocalDateTime.now());
        return passed.compareTo(COOLDOWN) >= 0 ? Duration.ZERO : COOLDOWN.minus(passed);
    }
}
