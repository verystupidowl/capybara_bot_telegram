package ru.tggc.capybaratelegrambot.utils;

import lombok.experimental.UtilityClass;
import ru.tggc.capybaratelegrambot.domain.model.BigJob;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.Cheerfulness;
import ru.tggc.capybaratelegrambot.domain.model.Happiness;
import ru.tggc.capybaratelegrambot.domain.model.Job;
import ru.tggc.capybaratelegrambot.domain.model.enums.JobType;
import ru.tggc.capybaratelegrambot.domain.model.Level;
import ru.tggc.capybaratelegrambot.domain.model.Satiety;
import ru.tggc.capybaratelegrambot.domain.model.Tea;
import ru.tggc.capybaratelegrambot.domain.model.enums.Type;
import ru.tggc.capybaratelegrambot.domain.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class CapybaraBuilder {

    public static Capybara buildCapybara(int size, String chatId, User user) {
        String name = "Моя капибара" + (size == 0 ? "" : " (" + size + ")");
        Level level = Level.builder()
                .type(Type.FIRST)
                .value(0)
                .maxValue(10)
                .build();
        Happiness happiness = Happiness.builder()
                .lastTime(LocalDateTime.now())
                .nextTime(LocalDateTime.now())
                .maxLevel(100)
                .level(0)
                .build();
        Satiety satiety = Satiety.builder()
                .lastTime(LocalDateTime.now())
                .nextTime(LocalDateTime.now())
                .maxLevel(100)
                .level(0)
                .build();
        Tea tea = Tea.builder()
                .isWaiting(false)
                .lastTime(LocalDateTime.now())
                .nextTime(LocalDateTime.now())
                .build();
        Cheerfulness cheerfulness = Cheerfulness.builder()
                .cheerfulnessLevel(100)
                .maxLevel(100)
                .nextTime(LocalDateTime.now())
                .build();
        BigJob bigJob = BigJob.builder()
                .active(false)
                .isOnBigJob(false)
                .build();
        Job job = Job.builder()
                .index(0)
                .rise(0)
                .isWorking(false)
                .bigJob(bigJob)
                .lastTime(LocalDateTime.now())
                .nextTime(LocalDateTime.now())
                .jobType(JobType.NONE)
                .build();
        return Capybara.builder()
                .name(name)
                .wins(0)
                .defeats(0)
                .currency(100)
                .created(LocalDateTime.now())
                .level(level)
                .happiness(happiness)
                .satiety(satiety)
                .user(user)
                .tea(tea)
                .photo(RandomUtils.getRandomPhoto())
                .cheerfulness(cheerfulness)
                .chatId(chatId)
                .job(job)
                .build();
    }
}
