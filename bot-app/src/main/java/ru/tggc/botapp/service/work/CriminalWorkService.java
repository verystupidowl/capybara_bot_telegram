package ru.tggc.botapp.service.work;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.Work;
import ru.tggc.botapp.domain.model.enums.WorkType;
import ru.tggc.botapp.formatter.FormatService;
import ru.tggc.botapp.formatter.msgkey.WorkMsgKey;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class CriminalWorkService extends AbstractWorkService {
    @Value("${bot.photos.work.criminal}")
    private String photo;

    private final FormatService formatService;

    public CriminalWorkService(FormatService formatService) {
        super(formatService);
        this.formatService = formatService;
    }

    @Override
    public List<String> takeFromWork(Capybara capybara) {
        checkHasWork(capybara);
        Work work = capybara.getWork();
        work.getWorkAction().takeFromWork();

        int salary = getJobType().getCalculateSalary().apply(work.getIndex());
        List<String> messages = new ArrayList<>();
        if (salary != -1) {
            capybara.setCurrency(capybara.getCurrency() + salary);
            messages.add(formatService.get(WorkMsgKey.TAKE_FROM_WORK, salary));
            work.setRise(work.getRise() + 1);
            if (checkRise(capybara)) {
                messages.add(formatService.get(WorkMsgKey.NEW_RISE));
            }
        } else {
            capybara.setCurrency(capybara.getCurrency() - capybara.getCurrency() / 10);
            messages.add(formatService.get(WorkMsgKey.BUSTED));
        }

        return messages;
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
    public WorkType getJobType() {
        return WorkType.CRIMINAL;
    }
}
