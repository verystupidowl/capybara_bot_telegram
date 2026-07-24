package ru.tggc.botapp.service.work;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tggc.botapp.domain.model.enums.WorkType;
import ru.tggc.botapp.service.stats.CapybaraStatsService;
import ru.tggc.telegrambotcore.formatter.FormatService;

import java.time.Duration;

@Service
public class ItWorkService extends AbstractWorkService {
    @Value("${bot.photos.work.setter.it}")
    private String photo;

    public ItWorkService(FormatService formatService, CapybaraStatsService statsService) {
        super(formatService, statsService);
    }

    @Override
    public WorkType getWorkType() {
        return WorkType.IT;
    }

    @Override
    protected String getSetWorkPhoto() {
        return photo;
    }

    @Override
    protected Duration getWorkCooldown() {
        return Duration.ofMinutes(15);
    }

    @Override
    protected Duration getWorkDuration() {
        return Duration.ofMinutes(90);
    }
}
