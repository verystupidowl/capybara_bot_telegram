package ru.tggc.capybaratelegrambot.mapper;

import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.domain.dto.MyCapybaraDto;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.Wedding;

@Component
public class MyCapybaraMapper implements Mappable<Capybara, MyCapybaraDto> {

    @Override
    public MyCapybaraDto toDto(Capybara capybara) {
        Wedding wedding = capybara.getWedding();
        String weddingName = null;
        if (wedding != null && wedding.getActive() && !wedding.getFirstCapybara().getName().equals(capybara.getName())) {
            weddingName = wedding.getFirstCapybara().getName();
        } else if (wedding != null && wedding.getActive() && !wedding.getSecondCapybara().getName().equals(capybara.getName())) {
            weddingName = wedding.getSecondCapybara().getName();
        }
        return MyCapybaraDto.builder()
                .name(capybara.getName())
                .level(capybara.getLevel().getValue())
                .type(capybara.getLevel().getType().getLabel())
                .cheerfulness(capybara.getCheerfulness().getCheerfulnessLevel())
                .job(capybara.getJob().getJobType().getLabel())
                .currency(capybara.getCurrency())
                .wedding(weddingName)
                .satietyLevel(capybara.getSatiety().getLevel())
                .satietyMaxLevel(100 + ((capybara.getLevel().getValue() / 10) * 10 * 2))
                .happinessLevel(capybara.getHappiness().getLevel())
                .happinessMaxLevel((100 + ((capybara.getLevel().getValue() / 10) * 10 * 2)))
                .wins(capybara.getWins())
                .defeats(capybara.getDefeats())
                .build();
    }

    @Override
    public Capybara fromDto(MyCapybaraDto capybaraDto) {
        return null;
    }
}
