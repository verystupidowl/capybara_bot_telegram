package ru.tggc.botapp.service.stats;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.botapp.domain.dto.StatKey;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.timedaction.Happiness;
import ru.tggc.telegrambotcore.formatter.FormatService;

import static java.lang.Math.max;

@Service
public class HappinessStatsService extends AbstractPropertyService<Happiness> {

    public HappinessStatsService(FormatService formatService, ApplicationEventPublisher eventPublisher) {
        super(formatService, eventPublisher);
    }

    @Override
    @Transactional
    public void modifyProperty(Capybara capybara, Integer value) {
        Happiness happiness = capybara.getHappiness();
        happiness.setLevel(max(0, happiness.getLevel() + value));
    }

    @Override
    @Transactional
    public boolean checkNewLevel(Capybara capybara) {
        Happiness happiness = capybara.getHappiness();
        return happiness.getLevel() >= happiness.calculateMaxLevel(capybara.getLevel().getValue());
    }

    @Override
    @Transactional
    public void setToDefault(Happiness stat) {
        stat.setLevel(0);
    }

    @Override
    public StatKey<Happiness> getStatKey() {
        return StatKey.HAPPINESS;
    }
}
