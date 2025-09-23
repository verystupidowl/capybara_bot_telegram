package ru.tggc.capybaratelegrambot.mapper;

import ru.tggc.capybaratelegrambot.domain.dto.CapybaraTeaDto;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;

public class CapybaraTeaMapper {

    public CapybaraTeaDto toDto(Capybara capybara) {
        return new CapybaraTeaDto(capybara.getUser().getId(), capybara.getName());
    }
}
