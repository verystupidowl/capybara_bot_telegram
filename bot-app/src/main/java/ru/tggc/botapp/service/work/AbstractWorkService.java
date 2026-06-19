package ru.tggc.botapp.service.work;

import lombok.RequiredArgsConstructor;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.Work;
import ru.tggc.botapp.domain.model.enums.WorkType;
import ru.tggc.botapp.domain.model.timedaction.WorkAction;
import ru.tggc.botapp.exceptions.CapybaraException;
import ru.tggc.botapp.formatter.FormatService;
import ru.tggc.botapp.formatter.msgkey.WorkMsgKey;
import ru.tggc.botapp.service.WorkService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static ru.tggc.telegrambotframework.util.Utils.throwIf;


@RequiredArgsConstructor
public abstract class AbstractWorkService implements WorkService {
    private final FormatService formatService;

    @Override
    public List<String> takeFromWork(Capybara capybara) {
        checkHasWork(capybara);
        Work work = capybara.getWork();
        work.getWorkAction().takeFromWork();

        int salary = getJobType().getCalculateSalary().apply(work.getIndex());
        capybara.setCurrency(capybara.getCurrency() + salary);

        List<String> messages = new ArrayList<>();
        messages.add(formatService.get(WorkMsgKey.TAKE_FROM_WORK, salary));

        work.setRise(work.getRise() + 1);
        if (checkRise(capybara)) {
            messages.add(formatService.get(WorkMsgKey.NEW_RISE));
        }

        return messages;
    }

    @Override
    public void dismissal(Capybara capybara) {
        checkHasWork(capybara);
        Work work = Work.builder()
                .workType(WorkType.NONE)
                .build();
        capybara.setWork(work);
    }

    @Override
    public String setWork(Capybara capybara) {
        checkHasNoWork(capybara);
        WorkAction workAction = new WorkAction(getWorkDuration(), getWorkCooldown());
        Work work = Work.builder()
                .workType(getJobType())
                .index(0)
                .workAction(workAction)
                .rise(1)
                .build();
        capybara.setWork(work);
        return getSetWorkPhoto();
    }

    protected abstract String getSetWorkPhoto();

    protected abstract Duration getWorkCooldown();

    protected abstract Duration getWorkDuration();

    @Override
    public void goWork(Capybara capybara) {
        checkHasWork(capybara);
        capybara.getWork().getWorkAction().startWorking();
    }


    protected boolean checkRise(Capybara capybara) {
        Work work = capybara.getWork();
        if (work.getRise() >= 10 * (work.getIndex() + 1) && work.getIndex() <= 5) {
            capybara.getWork().setRise(1);
            capybara.getWork().setIndex(capybara.getWork().getIndex() + 1);
            capybara.setCurrency(capybara.getCurrency() + 150);
            return true;
        }
        return false;
    }

    protected void checkHasWork(Capybara capybara) {
        throwIf(!checkWork(capybara), () -> {
            String message = formatService.get(WorkMsgKey.ERROR_HAS_NO_WORK);
            return new CapybaraException(message);
        });
    }

    protected void checkHasNoWork(Capybara capybara) {
        throwIf(checkWork(capybara), () -> {
            String message = formatService.get(WorkMsgKey.ERROR_ALREADY_HAS_WORK);
            return new CapybaraException(message);
        });
    }

    protected boolean checkWork(Capybara capybara) {
        return WorkType.NONE != capybara.getWork().getWorkType();
    }
}
