package ru.tggc.capybaratelegrambot.mapper;

import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.domain.dto.MyCapybaraDto;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.enums.WorkType;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.WorkAction;
import ru.tggc.capybaratelegrambot.utils.Utils;

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
                .canGoWork(Utils.getOr(capybara.getWork().getWorkAction(), WorkAction::canPerform, false))
                .canTakeFromWork(Utils.getOr(capybara.getWork().getWorkAction(), WorkAction::canTakeFrom, false))
                .canHappy(capybara.getHappiness().canPerform())
                .canSatiety(capybara.getSatiety().canPerform())
                .hasWork(capybara.getWork().getWorkType() != WorkType.NONE)
                .photoUrl(capybara.getPhoto().getUrl())
                .build();
    }
}
