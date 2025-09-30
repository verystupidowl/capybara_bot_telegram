package ru.tggc.capybaratelegrambot.utils;

import lombok.experimental.UtilityClass;
import ru.tggc.capybaratelegrambot.domain.model.BigJob;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.Chat;
import ru.tggc.capybaratelegrambot.domain.model.Improvement;
import ru.tggc.capybaratelegrambot.domain.model.Level;
import ru.tggc.capybaratelegrambot.domain.model.Race;
import ru.tggc.capybaratelegrambot.domain.model.User;
import ru.tggc.capybaratelegrambot.domain.model.Work;
import ru.tggc.capybaratelegrambot.domain.model.enums.ImprovementValue;
import ru.tggc.capybaratelegrambot.domain.model.enums.Type;
import ru.tggc.capybaratelegrambot.domain.model.enums.WorkType;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.Happiness;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.RaceAction;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.Satiety;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.Tea;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.WorkAction;

import java.time.LocalDateTime;

@UtilityClass
public class CapybaraBuilder {

    public static Capybara buildCapybara(int size, Chat chat, User user) {
        String name = "Моя капибара" + (size == 0 ? "" : " (" + size + ")");
        Improvement improvement = Improvement.builder()
                .improvementValue(ImprovementValue.NONE)
                .build();
        Level level = Level.builder()
                .type(Type.FIRST)
                .value(0)
                .maxValue(10)
                .build();
        Happiness happiness = Happiness.builder()
                .maxLevel(100)
                .level(0)
                .build();
        Satiety satiety = Satiety.builder()
                .maxLevel(100)
                .level(0)
                .build();
        Tea tea = Tea.builder()
                .capybara(null)
                .isWaiting(false)
                .build();
        BigJob bigJob = BigJob.builder()
                .active(false)
                .isOnBigJob(false)
                .build();
        Work work = Work.builder()
                .index(0)
                .rise(0)
                .workAction(new WorkAction())
                .workType(WorkType.NONE)
                .build();
        Race race = Race.builder()
                .defeats(0)
                .wins(0)
                .raceAction(new RaceAction(5))
                .build();
        return Capybara.builder()
                .name(name)
                .currency(100L)
                .created(LocalDateTime.now())
                .level(level)
                .happiness(happiness)
                .satiety(satiety)
                .user(user)
                .tea(tea)
                .photo(RandomUtils.getRandomDefaultPhoto())
                .race(race)
                .chat(chat)
                .work(work)
                .improvement(improvement)
                .build();
    }
}
