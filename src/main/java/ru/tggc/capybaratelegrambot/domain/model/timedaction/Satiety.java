package ru.tggc.capybaratelegrambot.domain.model.timedaction;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Satiety implements TimedAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer level;
    private Integer maxLevel;

    private LocalDateTime lastFed;

    private static final Duration COOLDOWN = Duration.ofMinutes(2);

    @Override
    public boolean canPerform() {
        return lastFed == null || Duration.between(lastFed, LocalDateTime.now()).compareTo(COOLDOWN) >= 0;
    }

    @Override
    public Duration timeUntilNext() {
        if (lastFed == null) return Duration.ZERO;
        Duration passed = Duration.between(lastFed, LocalDateTime.now());
        return passed.compareTo(COOLDOWN) >= 0 ? Duration.ZERO : COOLDOWN.minus(passed);
    }
}
