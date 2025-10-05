package ru.tggc.capybaratelegrambot.domain.model.timedaction;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;

import java.time.Duration;
import java.time.Instant;

@Data
@NoArgsConstructor
public class BigJobAction implements LongTimedAction {
    private Instant lastTaken;
    private Instant startTime;
    private Duration duration;
    private Duration cooldown;

    public BigJobAction(Duration duration, Duration cooldown) {
        this.duration = duration;
        this.cooldown = cooldown;
    }

    public void startWorking() {
        if (!canPerform()) {
            throw new CapybaraException("Нельзя отправить сейчас!");
        }
        startTime = Instant.now();
    }

    public void takeFromWork() {
        if (canTakeFrom()) {
            lastTaken = Instant.now();
            startTime = null;
        } else {
            throw new CapybaraException("Работа ещё не завершена!");
        }
    }

    @Override
    public Instant getStartTime() {
        return startTime;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    @Override
    public boolean isInProgress() {
        return startTime != null && Instant.now().isBefore(startTime.plus(duration));
    }

    @Override
    public Duration timeUntilFinish() {
        if (!isInProgress()) return Duration.ZERO;
        Instant end = startTime.plus(duration);
        return Duration.between(Instant.now(), end);
    }

    @Override
    public boolean canTakeFrom() {
        return isInProgress() && timeUntilFinish().isZero();
    }

    @Override
    public boolean canPerform() {
        if (isInProgress()) return false;
        if (lastTaken == null) return true;
        return Instant.now().isAfter(lastTaken.plus(cooldown));
    }

    @Override
    public Duration timeUntilNext() {
        if (isInProgress()) return timeUntilFinish();
        if (lastTaken == null) return Duration.ZERO;
        Instant nextAvailable = lastTaken.plus(cooldown);
        return Duration.between(Instant.now(), nextAvailable).isNegative()
                ? Duration.ZERO
                : Duration.between(Instant.now(), nextAvailable);
    }
}
