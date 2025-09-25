package ru.tggc.capybaratelegrambot.provider.impl;

import lombok.RequiredArgsConstructor;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.Work;
import ru.tggc.capybaratelegrambot.domain.model.enums.WorkType;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.WorkAction;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;
import ru.tggc.capybaratelegrambot.provider.JobProvider;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractJobProvider implements JobProvider {

    @Override
    public List<String> takeFromWork(Capybara capybara) {
        checkHasWork(capybara);
        Work work = capybara.getWork();
        if (work.getWorkAction().canTakeFrom()) {
            throw new CapybaraException("u cant take ur capy");
        }

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
    public void setJob(Capybara capybara) {
        checkHasNoWork(capybara);
        WorkAction workAction = new WorkAction(getWorkDuration(), getWorkCooldown());
        Work work = Work.builder()
                .workType(getJobType())
                .index(0)
                .workAction(workAction)
                .rise(1)
                .build();
        capybara.setWork(work);
    }

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
        if (!checkWork(capybara)) {
            throw new CapybaraException("Capybara has no work!");
        }
    }

    protected void checkHasNoWork(Capybara capybara) {
        if (checkWork(capybara)) {
            throw new CapybaraException("Capybara already has work!");
        }
    }

    protected boolean checkWork(Capybara capybara) {
        return WorkType.NONE == capybara.getWork().getWorkType();
    }
}
