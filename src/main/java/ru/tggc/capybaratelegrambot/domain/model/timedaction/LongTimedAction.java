package ru.tggc.capybaratelegrambot.domain.model.timedaction;

import java.time.Duration;
import java.time.Instant;

public interface LongTimedAction extends TimedAction {

    /**
     * Когда действие началось
     */
    Instant getStartTime();

    /**
     * Сколько длится выполнение
     */
    Duration getDuration();

    /**
     * true, если действие всё ещё идёт
     */
    boolean isInProgress();

    /**
     * Сколько осталось до завершения
     */
    Duration timeUntilFinish();
}
