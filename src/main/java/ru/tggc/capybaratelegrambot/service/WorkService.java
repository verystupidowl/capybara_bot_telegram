package ru.tggc.capybaratelegrambot.service;

import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.enums.WorkType;

import java.util.List;

public interface WorkService {

    String setWork(Capybara capybara);

    void goWork(Capybara capybara);

    WorkType getJobType();

    List<String> takeFromWork(Capybara capybara);

    void dismissal(Capybara capybara);
}
