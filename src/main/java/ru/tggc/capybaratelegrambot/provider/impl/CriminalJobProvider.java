package ru.tggc.capybaratelegrambot.provider.impl;

import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.Work;
import ru.tggc.capybaratelegrambot.domain.model.enums.WorkType;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class CriminalJobProvider extends AbstractJobProvider {

    @Override
    public List<String> takeFromWork(Capybara capybara) {
        checkHasWork(capybara);
        Work work = capybara.getWork();
        if (work.getWorkAction().canTakeFrom()) {
            throw new CapybaraException("u cant take ur capy from the work");
        }

        int salary = getJobType().getCalculateSalary().apply(work.getIndex());
        List<String> messages = new ArrayList<>();
        if (salary != -1) {
            capybara.setCurrency(capybara.getCurrency() + salary);
            messages.add("Ты забрал капибару с работы. Она получила целых " + salary + " арбузных долек!");
            if (checkRise(capybara)) {
                messages.add("Ух ты! Твоя капибара так усердно работала, что смогла получить повышение!" +
                        "\nПлюс 150 арбузных долек!!!");
            }
        } else {
            capybara.setCurrency(capybara.getCurrency() - capybara.getCurrency() / 10);
            messages.add("Твою капибару поймали! У нее забрали 10% долек...");
        }

        return messages;
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
