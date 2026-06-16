package ru.tggc.botapp.service;

import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.enums.WorkType;

import java.util.List;

public interface WorkService {

    String setWork(Capybara capybara);

    void goWork(Capybara capybara);

    WorkType getJobType();

    List<String> takeFromWork(Capybara capybara);

    void dismissal(Capybara capybara);
}
