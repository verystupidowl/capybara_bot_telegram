package ru.tggc.botapp.mapper;

import org.springframework.stereotype.Component;
import ru.tggc.botapp.domain.dto.CapybaraTeaDto;
import ru.tggc.botapp.domain.model.Capybara;

@Component
public class CapybaraTeaMapper {

    public CapybaraTeaDto toDto(Capybara capybara) {
        return new CapybaraTeaDto(capybara.getUser().getId(), capybara.getName());
    }
}
