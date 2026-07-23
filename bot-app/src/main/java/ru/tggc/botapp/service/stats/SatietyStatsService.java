package ru.tggc.botapp.service.stats;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.botapp.domain.dto.StatKey;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.timedaction.Satiety;

import static java.lang.Math.max;

@Service
public class SatietyStatsService implements CapybaraStats<Satiety> {

    @Override
    @Transactional
    public void modify(Capybara capybara, Integer value) {
        Satiety satiety = capybara.getSatiety();
        satiety.setLevel(max(0, satiety.getLevel() + value));
    }

    @Override
    @Transactional
    public boolean checkNewLevel(Capybara capybara) {
        Satiety satiety = capybara.getSatiety();
        return satiety.getLevel() >= satiety.calculateMaxLevel(capybara.getLevel().getValue());
    }

    @Override
    @Transactional
    public void setToDefault(Satiety stat) {
        stat.setLevel(0);
    }

    @Override
    public StatKey<Satiety> getStatKey() {
        return StatKey.SATIETY;
    }
}
