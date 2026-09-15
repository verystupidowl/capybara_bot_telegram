package ru.tggc.botapp.domain.model.timedaction;

import lombok.Data;
import jakarta.persistence.Embeddable;
import lombok.NoArgsConstructor;
import ru.tggc.botapp.exceptions.CapybaraException;

import java.time.Duration;
import java.time.Instant;

@Data
@Embeddable
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

    public void startBigJob() {
        if (!canPerform()) {
            throw new CapybaraException("Нельзя отправить сейчас!");
        }
        startTime = Instant.now();
    }

    public void takeFromBigJob() {
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
        return startTime != null;
    }

    @Override
    public Duration timeUntilFinish() {
        if (!isInProgress()) return Duration.ZERO;
        Instant end = startTime.plus(duration);
        Duration remaining = Duration.between(Instant.now(), end);
        return remaining.isNegative() ? Duration.ZERO : remaining;
    }

    @Override
    public boolean canTakeFrom() {
        return isInProgress() && duration != null && !Instant.now().isBefore(startTime.plus(duration));
    }

    @Override
    public boolean canPerform() {
        if (duration == null || cooldown == null) return false;
        if (isInProgress()) return false;
        if (lastTaken == null) return true;
        return !Instant.now().isBefore(lastTaken.plus(cooldown));
    }

    @Override
    public Duration timeUntilNext() {
        if (isInProgress()) return timeUntilFinish();
        if (lastTaken == null || cooldown == null) return Duration.ZERO;
        Instant nextAvailable = lastTaken.plus(cooldown);
        return Duration.between(Instant.now(), nextAvailable).isNegative()
                ? Duration.ZERO
                : Duration.between(Instant.now(), nextAvailable);
    }
}
