package ru.tggc.capybaratelegrambot.provider.impl;

import lombok.RequiredArgsConstructor;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.Work;
import ru.tggc.capybaratelegrambot.domain.model.enums.WorkType;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;
import ru.tggc.capybaratelegrambot.provider.JobProvider;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractJobProvider implements JobProvider {

    @Override
    public List<String> takeFromWork(Capybara capybara) {
        checkHasWork(capybara);
        Work work = capybara.getWork();
        checkCanTakeFromWork(work);

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
        Work work = Work.builder()
                .workType(getJobType())
                .index(0)
                .isWorking(true)
                .rise(1)
                .build();
        capybara.setWork(work);
    }

    @Override
    public void goWork(Capybara capybara) {
        checkHasWork(capybara);
        Work work = capybara.getWork();
        checkCanGoWork(work);

        work.setIsWorking(true);
        work.setTimer(LocalDateTime.now().plusHours(2));
        capybara.setWork(work);
    }

    protected static void checkCanTakeFromWork(Work work) {
        if (!work.getIsWorking()) {
            throw new CapybaraException("Capybara wsnt on work!");
        }
        if (LocalDateTime.now().isBefore(work.getTimer())) {
            throw new CapybaraException("U cant take ur capy yet!");
        }
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

    protected void checkCanGoWork(Work work) {
        if (work.getIsWorking()) {
            throw new CapybaraException("Capybara is already on work!");
        }
        if (work.getBigJob().getIsOnBigJob()) {
            throw new CapybaraException("Capybara is on big job!");
        }
        if (LocalDateTime.now().isBefore(work.getNextTime())) {
            String delta = String.valueOf(work.getNextTime().compareTo(LocalDateTime.now()));
            throw new CapybaraException("Capybara can go job only in " + delta);
        }
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
