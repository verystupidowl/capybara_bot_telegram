package ru.tggc.capybaratelegrambot.utils;

import lombok.experimental.UtilityClass;
import ru.tggc.capybaratelegrambot.domain.dto.FileDto;

import java.util.List;

import static ru.tggc.capybaratelegrambot.domain.dto.enums.FileType.DOC;
import static ru.tggc.capybaratelegrambot.domain.dto.enums.FileType.PHOTO;

@UtilityClass
public class CapybaraPhotos {
    public final List<String> DEFAULT_PHOTOS = List.of(
            "457239574",
            "457239456",
            "457239409",
            "457239396",
            "457239375",
            "457239334",
            "457239317",
            "457239262",
            "457239201",
            "457239189",
            "457239167",
            "457239164",
            "457239104",
            "457239095",
            "457239073",
            "457239037",
            "457239025"
    );

    public final List<FileDto> RACE_PHOTOS = List.of(
            new FileDto("https://vk.com/photo-209917797_457241919", PHOTO),
            new FileDto("https://vk.com/photo-209917797_457241924", PHOTO),
            new FileDto("https://vk.com/photo-209917797_457241923", PHOTO),
            new FileDto("https://vk.com/photo-209917797_457241922", PHOTO),
            new FileDto("https://vk.com/photo-209917797_457241921", PHOTO),
            new FileDto("https://vk.com/photo-209917797_457241920", PHOTO),
            new FileDto("CgACAgQAAyEFAAS-gX0XAAIHtGjYEbh8dKraYQiUcwmSLVmUVDYfAAIVAwAC1nKsUYoLomnV0FyENgQ", DOC),
            new FileDto("CgACAgQAAyEFAAS-gX0XAAIHtmjYEz6sNvRTpm6fxpdXTztLTgtZAAJ1AwACxpoFU0qXGu-MvYwDNgQ", DOC),
            new FileDto("CgACAgQAAyEFAAS-gX0XAAIHt2jYE10Ytug2qfLFbZioVsSOFBMpAAL3AgAC-40MU9lbtg_EuWUxNgQ", DOC)
    );
}
