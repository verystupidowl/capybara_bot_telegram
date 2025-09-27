package ru.tggc.capybaratelegrambot.mapper;

import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.domain.dto.MyCapybaraDto;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.enums.WorkType;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.WorkAction;

import java.util.function.Function;

import static ru.tggc.capybaratelegrambot.utils.Utils.getOr;

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
                .stamina(capybara.getRaceAction().getStaminaPercent())
                .job(capybara.getWork().getWorkType().getLabel())
                .currency(capybara.getCurrency())
                .wedding(weddingName)
                .satietyLevel(capybara.getSatiety().getLevel())
                .satietyMaxLevel(100 + ((capybara.getLevel().getValue() / 10) * 10 * 2))
                .happinessLevel(capybara.getHappiness().getLevel())
                .happinessMaxLevel((100 + ((capybara.getLevel().getValue() / 10) * 10 * 2)))
                .wins(capybara.getWins())
                .defeats(capybara.getDefeats())
                .canGoWork(getOr(capybara.getWork().getWorkAction(), WorkAction::canPerform, false))
                .canTakeFromWork(getOr(capybara.getWork().getWorkAction(), WorkAction::canTakeFrom, false))
                .canHappy(capybara.getHappiness().canPerform())
                .canSatiety(capybara.getSatiety().canPerform())
                .hasWork(capybara.getWork().getWorkType() != WorkType.NONE)
                .photo(getOr(capybara.getPhoto().getFileId(), Function.identity(), capybara.getPhoto().getUrl()))
                .build();
    }
}
