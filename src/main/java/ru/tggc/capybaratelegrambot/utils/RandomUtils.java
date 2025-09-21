package ru.tggc.capybaratelegrambot.utils;

import lombok.experimental.UtilityClass;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraDefaultPhoto;
import ru.tggc.capybaratelegrambot.domain.dto.HappinessThings;
import ru.tggc.capybaratelegrambot.domain.model.Photo;

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

    public Photo getRandomPhoto() {
        CapybaraDefaultPhoto[] values = CapybaraDefaultPhoto.values();
        String url = values[RANDOM.nextInt(values.length)].getUrl();
        return Photo.builder()
                .url(url)
                .build();
    }

    public int getRandomInt() {
        return RANDOM.nextInt();
    }

    public int getRandomInt(int seed) {
        return RANDOM.nextInt(seed);
    }

    public static CasinoTargetType randomWeighted() {
        double r = RANDOM.nextDouble();

        return switch (r) {
            case double v when v < 1.0 / 37 -> ZERO;
            case double v when v < (1.0 / 37) + (18.0 / 37) -> RED;
            default -> BLACK;
        };
    }
}
