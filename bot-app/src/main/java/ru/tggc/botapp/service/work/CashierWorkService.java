package ru.tggc.botapp.service.work;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tggc.botapp.domain.model.enums.work.WorkType;
import ru.tggc.botapp.service.stats.CapybaraStatsService;
import ru.tggc.telegrambotcore.formatter.FormatService;

import java.time.Duration;

@Service
public class CashierWorkService extends AbstractWorkService {
    @Value("${bot.photos.work.setter.cashier}")
    private String photo;

    public CashierWorkService(FormatService formatService, CapybaraStatsService statsService) {
        super(formatService, statsService);
    }

    @Override
    public WorkType getWorkType() {
        return WorkType.CASHIER;
    }

    @Override
    protected String getSetWorkPhoto() {
        return photo;
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
