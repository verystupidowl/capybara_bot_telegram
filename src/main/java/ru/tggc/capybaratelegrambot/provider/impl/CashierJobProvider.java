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
    protected String getSetWorkPhoto() {
        return "https://vk.com/photo-209917797_457242285";
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
