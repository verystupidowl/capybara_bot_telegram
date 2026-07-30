package ru.tggc.botapp.service.work;

import lombok.RequiredArgsConstructor;
import ru.tggc.botapp.domain.dto.StatKey;
import ru.tggc.botapp.domain.model.BigJob;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.Work;
import ru.tggc.botapp.domain.model.enums.work.WorkType;
import ru.tggc.botapp.domain.model.timedaction.BigJobAction;
import ru.tggc.botapp.domain.model.timedaction.WorkAction;
import ru.tggc.botapp.exceptions.CapybaraException;
import ru.tggc.botapp.formatter.msgkey.WorkMsgKey;
import ru.tggc.botapp.service.WorkService;
import ru.tggc.botapp.service.stats.CapybaraStatsService;
import ru.tggc.telegrambotcore.formatter.FormatService;

import java.time.Duration;

import static ru.tggc.telegrambotcore.util.Utils.throwIf;


@RequiredArgsConstructor
public abstract class AbstractWorkService implements WorkService {
    private final FormatService formatService;
    private final CapybaraStatsService statsService;

    @Override
    public String takeFromWork(Capybara capybara) {
        Work work = capybara.getWork();
        checkHasWork(work);
        work.getWorkAction().takeFromWork();

        int salary = getWorkType().calculateSalary(work.getIndex());
        capybara.increaseMoney(salary);

        statsService.modify(capybara, StatKey.RISE, 1);

        return formatService.get(WorkMsgKey.TAKE_FROM_WORK, salary);
    }

    @Override
    public void dismissal(Capybara capybara) {
        Work work = capybara.getWork();
        checkHasWork(work);

        work.setWorkType(WorkType.NONE);
        work.setWorkAction(new WorkAction());
        work.setRise(0);
        work.setIndex(0);

        BigJob bigJob = work.getBigJob();
        bigJob.setActive(false);
        bigJob.setBigJobAction(new BigJobAction());

        capybara.setWork(work);
    }

    @Override
    public String setWork(Capybara capybara) {
        WorkAction workAction = new WorkAction(getWorkDuration(), getWorkCooldown());
        Work work = capybara.getWork();
        checkHasNoWork(work);
        work.setWorkAction(workAction);
        work.setRise(1);
        work.setIndex(0);
        work.setWorkType(getWorkType());
        capybara.setWork(work);
        return getSetWorkPhoto();
    }

    @Override
    public void goWork(Capybara capybara) {
        Work work = capybara.getWork();
        checkHasWork(work);
        capybara.getWork().getWorkAction().startWorking();
    }

    protected abstract String getSetWorkPhoto();

    protected abstract Duration getWorkCooldown();

    protected abstract Duration getWorkDuration();

    protected void checkHasWork(Work work) {
        throwIf(!work.hasWork(), () -> {
            String message = formatService.get(WorkMsgKey.ERROR_HAS_NO_WORK);
            return new CapybaraException(message);
        });
    }

    protected void checkHasNoWork(Work work) {
        throwIf(work.hasWork(), () -> {
            String message = formatService.get(WorkMsgKey.ERROR_ALREADY_HAS_WORK);
            return new CapybaraException(message);
        });
    }
}
