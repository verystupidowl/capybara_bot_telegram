package ru.tggc.capybaratelegrambot.provider.impl;

import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.domain.model.enums.WorkType;

import java.time.Duration;

@Service
public class CashierJobProvider extends AbstractJobProvider {

    @Override
    public WorkType getJobType() {
        return WorkType.CASHIER;
    }

    @Override
    protected Duration getWorkCooldown() {
        return Duration.ofMinutes(30);
    }

    @Override
    protected Duration getWorkDuration() {
        return Duration.ofMinutes(30);
    }
}
