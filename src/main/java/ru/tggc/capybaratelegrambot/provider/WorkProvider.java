package ru.tggc.capybaratelegrambot.provider;

import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.enums.WorkType;

import java.util.List;

public interface WorkProvider {

    String setJob(Capybara capybara);

    void goWork(Capybara capybara);

    WorkType getJobType();

    List<String> takeFromWork(Capybara capybara);

    void dismissal(Capybara capybara);
}
