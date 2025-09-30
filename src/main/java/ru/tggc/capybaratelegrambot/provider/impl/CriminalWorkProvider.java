package ru.tggc.capybaratelegrambot.provider.impl;

import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.Work;
import ru.tggc.capybaratelegrambot.domain.model.enums.WorkType;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;
import ru.tggc.capybaratelegrambot.utils.Text;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static ru.tggc.capybaratelegrambot.utils.Utils.throwIf;

@Service
public class CriminalWorkProvider extends AbstractWorkProvider {

    @Override
    public List<String> takeFromWork(Capybara capybara) {
        checkHasWork(capybara);
        Work work = capybara.getWork();
        throwIf(!work.getWorkAction().canTakeFrom(), () -> new CapybaraException("u cant take ur capy from the work"));

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
            messages.add(Text.BUSTED);
        }

        return messages;
    }

    @Override
    protected String getSetWorkPhoto() {
        return "https://vk.com/photo-209917797_457242283";
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
