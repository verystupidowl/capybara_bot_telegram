package ru.tggc.capybaratelegrambot.mapper;

import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraTeaDto;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;

@Component
public class CapybaraTeaMapper {

    public CapybaraTeaDto toDto(Capybara capybara) {
        return new CapybaraTeaDto(capybara.getUser().getId().toString(), capybara.getName());
    }
}
