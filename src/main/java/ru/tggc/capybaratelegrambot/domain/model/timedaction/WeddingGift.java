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
public class WeddingGift implements TimedAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime lastTime;
    private Integer level;
    private LocalDateTime nextTime;

    @Override
    public boolean canPerform() {
        return false;
    }

    @Override
    public Duration timeUntilNext() {
        return null;
    }
}
