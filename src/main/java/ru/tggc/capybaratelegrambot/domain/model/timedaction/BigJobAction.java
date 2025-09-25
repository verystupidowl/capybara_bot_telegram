package ru.tggc.capybaratelegrambot.domain.model.timedaction;

import lombok.Data;

import java.time.Duration;
import java.time.Instant;

@Data
public class BigJobAction implements LongTimedAction {

    @Override
    public Instant getStartTime() {
        return null;
    }

    @Override
    public Duration getDuration() {
        return null;
    }

    @Override
    public boolean isInProgress() {
        return false;
    }

    @Override
    public Duration timeUntilFinish() {
        return null;
    }

    @Override
    public boolean canTakeFrom() {
        return false;
    }

    @Override
    public boolean canPerform() {
        return false;
    }

    @Override
    public Duration timeUntilNext() {
        return null;
    }
}
