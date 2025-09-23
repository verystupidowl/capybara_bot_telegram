package ru.tggc.capybaratelegrambot.mapper;

import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.domain.dto.MyCapybaraDto;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;

@Component
public class MyCapybaraMapper {

    public MyCapybaraDto toDto(Capybara capybara) {
        Capybara wedding = capybara.getSpouse();
        String weddingName = null;
        if (wedding != null) {
            weddingName = wedding.getName();
        }
        return MyCapybaraDto.builder()
                .name(capybara.getName())
                .level(capybara.getLevel().getValue())
                .type(capybara.getLevel().getType().getLabel())
                .cheerfulness(capybara.getCheerfulness().getCheerfulnessLevel())
                .job(capybara.getWork().getWorkType().getLabel())
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
}
