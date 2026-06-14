package ru.tggc.botapp.util;

import lombok.experimental.UtilityClass;
import ru.tggc.botapp.domain.model.BigJob;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.Chat;
import ru.tggc.botapp.domain.model.Fight;
import ru.tggc.botapp.domain.model.Improvement;
import ru.tggc.botapp.domain.model.Level;
import ru.tggc.botapp.domain.model.Race;
import ru.tggc.botapp.domain.model.User;
import ru.tggc.botapp.domain.model.Work;
import ru.tggc.botapp.domain.model.enums.ImprovementValue;
import ru.tggc.botapp.domain.model.enums.Type;
import ru.tggc.botapp.domain.model.enums.WorkType;
import ru.tggc.botapp.domain.model.enums.fight.FightBuffHeal;
import ru.tggc.botapp.domain.model.enums.fight.FightBuffShield;
import ru.tggc.botapp.domain.model.enums.fight.FightBuffSpecial;
import ru.tggc.botapp.domain.model.enums.fight.FightBuffWeapon;
import ru.tggc.botapp.domain.model.timedaction.BigJobAction;
import ru.tggc.botapp.domain.model.timedaction.FightAction;
import ru.tggc.botapp.domain.model.timedaction.Happiness;
import ru.tggc.botapp.domain.model.timedaction.RaceAction;
import ru.tggc.botapp.domain.model.timedaction.Satiety;
import ru.tggc.botapp.domain.model.timedaction.Tea;
import ru.tggc.botapp.domain.model.timedaction.WorkAction;

import java.time.Duration;
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
                .bigJobAction(new BigJobAction())
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
        Fight fight = Fight.builder()
                .loses(0)
                .wins(0)
                .heal(FightBuffHeal.NONE)
                .shield(FightBuffShield.NONE)
                .weapon(FightBuffWeapon.NONE)
                .special(FightBuffSpecial.NONE)
                .fightAction(new FightAction(Duration.ofHours(2)))
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
                .fight(fight)
                .build();
    }
}
