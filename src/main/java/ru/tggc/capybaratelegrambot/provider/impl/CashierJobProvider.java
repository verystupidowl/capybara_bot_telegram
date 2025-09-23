package ru.tggc.capybaratelegrambot.provider.impl;

import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.domain.model.enums.WorkType;

@Service
public class CashierJobProvider extends AbstractJobProvider {

    @Override
    public WorkType getJobType() {
        return WorkType.CASHIER;
    }
}
