package ru.tggc.botapp.service.work;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tggc.botapp.domain.dto.StatKey;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.Work;
import ru.tggc.botapp.domain.model.enums.WorkType;
import ru.tggc.botapp.formatter.msgkey.WorkMsgKey;
import ru.tggc.botapp.service.stats.CapybaraStatsService;
import ru.tggc.telegrambotcore.formatter.FormatService;

import java.time.Duration;

@Service
public class CriminalWorkService extends AbstractWorkService {
    @Value("${bot.photos.work.setter.criminal}")
    private String photo;

    private final FormatService formatService;
    private final CapybaraStatsService statsService;

    public CriminalWorkService(FormatService formatService, CapybaraStatsService statsService) {
        super(formatService, statsService);
        this.formatService = formatService;
        this.statsService = statsService;
    }

    @Override
    public String takeFromWork(Capybara capybara) {
        Work work = capybara.getWork();
        checkHasWork(work);
        work.getWorkAction().takeFromWork();

        int salary = getWorkType().calculateSalary(work.getIndex());
        if (salary != -1) {
            capybara.increaseMoney(salary);
            statsService.modify(capybara, StatKey.RISE, 1);
            return formatService.get(WorkMsgKey.TAKE_FROM_WORK, salary);
        } else {
            capybara.increaseMoney((int) (capybara.getCurrency() / 10));
            return formatService.get(WorkMsgKey.BUSTED);
        }
    }

    @Override
    protected String getSetWorkPhoto() {
        return photo;
    }

    @Override
    protected Duration getWorkCooldown() {
        return Duration.ofHours(3);
    }

    @Override
    protected Duration getWorkDuration() {
        return Duration.ofMinutes(90);
    }

    @Override
    public WorkType getWorkType() {
        return WorkType.CRIMINAL;
    }
}
