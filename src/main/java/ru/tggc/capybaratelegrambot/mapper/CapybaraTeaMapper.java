package ru.tggc.capybaratelegrambot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraTeaDto;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface CapybaraTeaMapper extends Mappable<Capybara, CapybaraTeaDto> {

    @Override
    @Mapping(source = "userId", target = "user.userId")
    CapybaraTeaDto toDto(Capybara capybara);
}
