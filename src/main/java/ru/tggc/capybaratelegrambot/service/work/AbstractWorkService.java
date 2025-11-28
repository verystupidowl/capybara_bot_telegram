package ru.tggc.capybaratelegrambot.service.work;

import lombok.RequiredArgsConstructor;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.Work;
import ru.tggc.capybaratelegrambot.domain.model.enums.WorkType;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.WorkAction;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;
import ru.tggc.capybaratelegrambot.service.WorkService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static ru.tggc.capybaratelegrambot.utils.Utils.throwIf;

@RequiredArgsConstructor
public abstract class AbstractWorkService implements WorkService {

    @Override
    public List<String> takeFromWork(Capybara capybara) {
        checkHasWork(capybara);
        Work work = capybara.getWork();
        work.getWorkAction().takeFromWork();

        int salary = getJobType().getCalculateSalary().apply(work.getIndex());
        capybara.setCurrency(capybara.getCurrency() + salary);

        List<String> messages = new ArrayList<>();
        messages.add("Ты забрал капибару с работы. Она получила целых " + salary + " арбузных долек!");

        if (checkRise(capybara)) {
            messages.add("Ух ты! Твоя капибара так усердно работала, что смогла получить повышение!" +
                    "\nПлюс 150 арбузных долек!!!");
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
        if (work.getRise() + 1 >= 10 * (work.getIndex() + 1) && work.getIndex() <= 5) {
            capybara.getWork().setRise(1);
            capybara.getWork().setIndex(capybara.getWork().getIndex() + 1);
            capybara.setCurrency(capybara.getCurrency() + 150);
            return true;
        }
        return false;
    }

    protected void checkHasWork(Capybara capybara) {
        throwIf(!checkWork(capybara), () -> new CapybaraException("Capybara has no work!"));
    }

    protected void checkHasNoWork(Capybara capybara) {
        throwIf(checkWork(capybara), () -> new CapybaraException("Capybara has no work!"));
    }

    protected boolean checkWork(Capybara capybara) {
        return WorkType.NONE != capybara.getWork().getWorkType();
    }
}
