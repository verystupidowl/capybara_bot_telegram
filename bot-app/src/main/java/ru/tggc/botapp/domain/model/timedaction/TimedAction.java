package ru.tggc.botapp.domain.model.timedaction;

import java.time.Duration;

public interface TimedAction {

    /**
     * @return true, если можно начать действие
     **/
    boolean canPerform();

    Duration timeUntilNext();
}
