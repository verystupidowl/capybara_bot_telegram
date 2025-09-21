package ru.tggc.capybaratelegrambot.provider;

import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.enums.JobType;

import java.util.List;

public interface JobProvider {
    void setJob(Capybara capybara);

    void goWork(Capybara capybara);

    JobType getJobType();

    List<String> takeFromWork(Capybara capybara);

    void dismissal(Capybara capybara);
}
