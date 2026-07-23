package ru.tggc.botapp.domain.model.timedaction;

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
public class WeddingGift implements TimedAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime lastTime;
    private Integer amount;

    private static final Duration COOLDOWN = Duration.ofDays(1);

    @Override
    public boolean canPerform() {
        return lastTime == null || Duration.between(lastTime, LocalDateTime.now()).compareTo(COOLDOWN) >= 0;
    }

    @Override
    public Duration timeUntilNext() {
        if (lastTime == null) return Duration.ZERO;
        Duration passed = Duration.between(lastTime, LocalDateTime.now());
        return passed.compareTo(COOLDOWN) >= 0 ? Duration.ZERO : COOLDOWN.minus(passed);
    }
}
