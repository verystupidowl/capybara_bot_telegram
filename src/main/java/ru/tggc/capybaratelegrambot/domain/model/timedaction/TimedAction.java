package ru.tggc.capybaratelegrambot.domain.model.timedaction;

import java.time.Duration;

public interface TimedAction {

    boolean canPerform();

    Duration timeUntilNext();
}
