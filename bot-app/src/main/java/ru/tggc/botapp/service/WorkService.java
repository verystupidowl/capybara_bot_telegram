package ru.tggc.botapp.service;

import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.enums.work.WorkType;

public interface WorkService {

    String setWork(Capybara capybara);

    void goWork(Capybara capybara);

    WorkType getWorkType();

    String takeFromWork(Capybara capybara);

    void dismissal(Capybara capybara);
}
