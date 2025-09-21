package ru.tggc.capybaratelegrambot.provider.impl;

import lombok.RequiredArgsConstructor;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.Job;
import ru.tggc.capybaratelegrambot.domain.model.enums.JobType;
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
        Job job = capybara.getJob();
        checkCanTakeFromWork(job);

        int salary = getJobType().getCalculateSalary().apply(job.getIndex());
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
        Job job = Job.builder()
                .jobType(JobType.NONE)
                .build();
        capybara.setJob(job);
    }

    @Override
    public void setJob(Capybara capybara) {
        checkHasNoWork(capybara);
        Job job = Job.builder()
                .jobType(getJobType())
                .index(0)
                .isWorking(true)
                .rise(1)
                .build();
        capybara.setJob(job);
    }

    @Override
    public void goWork(Capybara capybara) {
        checkHasWork(capybara);
        Job job = capybara.getJob();
        checkCanGoWork(job);

        job.setIsWorking(true);
        job.setTimer(LocalDateTime.now().plusHours(2));
        capybara.setJob(job);
    }

    protected static void checkCanTakeFromWork(Job job) {
        if (!job.getIsWorking()) {
            throw new CapybaraException("Capybara wsnt on work!");
        }
        if (LocalDateTime.now().isBefore(job.getTimer())) {
            throw new CapybaraException("U cant take ur capy yet!");
        }
    }


    protected boolean checkRise(Capybara capybara) {
        Job job = capybara.getJob();
        if (job.getRise() + 1 >= 10 * (job.getIndex() + 1) && job.getIndex() <= 5) {
            capybara.getJob().setRise(1);
            capybara.getJob().setIndex(capybara.getJob().getIndex() + 1);
            capybara.setCurrency(capybara.getCurrency() + 150);
            return true;
        }
        return false;
    }

    protected void checkCanGoWork(Job job) {
        if (job.getIsWorking()) {
            throw new CapybaraException("Capybara is already on work!");
        }
        if (job.getBigJob().getIsOnBigJob()) {
            throw new CapybaraException("Capybara is on big job!");
        }
        if (LocalDateTime.now().isBefore(job.getNextTime())) {
            String delta = String.valueOf(job.getNextTime().compareTo(LocalDateTime.now()));
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
        return JobType.NONE == capybara.getJob().getJobType();
    }
}
