package ru.tggc.botapp.service.work;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tggc.botapp.domain.model.enums.WorkType;
import ru.tggc.botapp.formatter.FormatService;

import java.time.Duration;

@Service
public class CashierWorkService extends AbstractWorkService {
    @Value("${bot.photos.work.cashier}")
    private String photo;

    public CashierWorkService(FormatService formatService) {
        super(formatService);
    }

    @Override
    public WorkType getJobType() {
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
