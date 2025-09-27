package ru.tggc.capybaratelegrambot.utils;

import lombok.experimental.UtilityClass;
import ru.tggc.capybaratelegrambot.domain.dto.FileDto;
import ru.tggc.capybaratelegrambot.domain.dto.enums.HappinessThings;
import ru.tggc.capybaratelegrambot.domain.model.Photo;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static ru.tggc.capybaratelegrambot.utils.CasinoTargetType.BLACK;
import static ru.tggc.capybaratelegrambot.utils.CasinoTargetType.RED;
import static ru.tggc.capybaratelegrambot.utils.CasinoTargetType.ZERO;

@UtilityClass
public class RandomUtils {
    private static final Random RANDOM = new Random();

    public HappinessThings getRandomHappinessThing() {
        HappinessThings[] values = HappinessThings.values();
        return values[RANDOM.nextInt(values.length)];
    }

    public Photo getRandomDefaultPhoto() {
        List<String> values = CapybaraPhotos.DEFAULT_PHOTOS;
        String id = values.get(RANDOM.nextInt(values.size()));
        String url = "https://vk.com/photo-206143282_" + id;
        return Photo.builder()
                .url(url)
                .build();
    }

    public FileDto getRandomRacePhoto() {
        return CapybaraPhotos.RACE_PHOTOS.get(RANDOM.nextInt(CapybaraPhotos.RACE_PHOTOS.size()));
    }

    public int getRandomInt() {
        return RANDOM.nextInt();
    }

    public int getRandomInt(int seed) {
        return RANDOM.nextInt(seed);
    }

    public static CasinoTargetType randomWeighted() {
        double r = RANDOM.nextDouble();
        return Map.of(
                        ZERO, 1.0 / 37,
                        RED, (1.0 / 37) + (18.0 / 37),
                        BLACK, 1.0
                )
                .entrySet()
                .stream()
                .filter(entry -> r < entry.getValue())
                .findFirst()
                .map(Map.Entry::getKey)
                .orElseThrow(IllegalArgumentException::new);
    }
}
