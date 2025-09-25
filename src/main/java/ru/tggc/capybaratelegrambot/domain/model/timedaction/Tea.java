package ru.tggc.capybaratelegrambot.domain.model.timedaction;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tea implements TimedAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(mappedBy = "tea")
    @ToString.Exclude
    private Capybara capybara;
    private boolean isWaiting;

    private LocalDateTime lastTea;

    private static final Duration COOLDOWN = Duration.ofMinutes(60);

    @Override
    public boolean canPerform() {
        return lastTea == null || Duration.between(lastTea, LocalDateTime.now()).compareTo(COOLDOWN) >= 0;
    }

    @Override
    public Duration timeUntilNext() {
        if (lastTea == null) return Duration.ZERO;
        Duration passed = Duration.between(lastTea, LocalDateTime.now());
        return passed.compareTo(COOLDOWN) >= 0 ? Duration.ZERO : COOLDOWN.minus(passed);
    }
}
