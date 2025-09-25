package ru.tggc.capybaratelegrambot.utils;

import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@UtilityClass
public class Utils {

    public static <T, R> R getOrNull(T t, Function<T, R> function) {
        return Optional.ofNullable(t).map(function).orElse(null);
    }

    public static <T, R> R getOr(T t, Function<T, R> function, R orElse) {
        return Optional.ofNullable(t).map(function).orElse(orElse);
    }

    public static <T> void ifPresent(T t, Consumer<T> consumer) {
        Optional.ofNullable(t).ifPresent(consumer);
    }

    public static String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        if (hours > 0) return hours + "ч " + minutes + "м";
        if (minutes > 0) return minutes + "м " + seconds + "с";
        return seconds + "с";
    }
}
