package ru.tggc.capybaratelegrambot.service;

import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.LongTimedAction;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.TimedAction;

import java.time.Duration;

import static ru.tggc.capybaratelegrambot.utils.Utils.formatDuration;


@Service
public class TimedActionService {

    public String getStatus(TimedAction action) {
        if (action.canPerform()) {
            return "0";
        }
        Duration remaining = action.timeUntilNext();
        return formatDuration(remaining);

    }

    public String getStatus(LongTimedAction action) {
        if (action.isInProgress()) {
            Duration remaining = action.timeUntilFinish();
            return formatDuration(remaining);
        } else if (action.canPerform()) {
            return "0";
        }
        Duration duration = action.timeUntilNext();
        return formatDuration(duration);
    }
}
