package ru.tggc.botapp.service.stats;

import ru.tggc.botapp.domain.dto.StatKey;
import ru.tggc.botapp.domain.model.Capybara;

public interface CapybaraStats<T> {

    void modify(Capybara capybara, Integer value);

    boolean checkNewLevel(Capybara capybara);

    void setToDefault(T stat);

    StatKey<T> getStatKey();
}
