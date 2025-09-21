package ru.tggc.capybaratelegrambot.provider.impl;

import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.domain.model.enums.JobType;

@Service
public class ItJobProvider extends AbstractJobProvider {

    @Override
    public JobType getJobType() {
        return JobType.PROGRAMMING;
    }
}
