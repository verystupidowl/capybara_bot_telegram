package ru.tggc.capybaratelegrambot.keyboard;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraInfoDto;
import ru.tggc.capybaratelegrambot.domain.dto.FightCapybaraDto;
import ru.tggc.capybaratelegrambot.domain.dto.MyCapybaraDto;
import ru.tggc.capybaratelegrambot.domain.model.enums.fight.BuffType;
import ru.tggc.capybaratelegrambot.domain.model.enums.fight.FightBuffEnum;
import ru.tggc.capybaratelegrambot.domain.model.enums.fight.FightBuffHeal;
import ru.tggc.capybaratelegrambot.domain.model.enums.fight.FightBuffShield;
import ru.tggc.capybaratelegrambot.domain.model.enums.fight.FightBuffSpecial;
import ru.tggc.capybaratelegrambot.domain.model.enums.fight.FightBuffWeapon;
import ru.tggc.capybaratelegrambot.provider.BossFightProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InlineKeyboardCreator {

    private final BossFightProvider bossFightProvider;

    public InlineKeyboardMarkup myCapybaraKeyboard(MyCapybaraDto capybara) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> change = new ArrayList<>();
        InlineKeyboardButton setName = new InlineKeyboardButton("Изменить имя").callbackData("set_name");
        change.add(setName);
        InlineKeyboardButton setPhoto = new InlineKeyboardButton("Изменить фото").callbackData("set_photo");
        change.add(setPhoto);

        rows.add(change);
        List<InlineKeyboardButton> feedHappy = null;

        if (Boolean.TRUE.equals(capybara.canSatiety())) {
            InlineKeyboardButton feedCapybara = new InlineKeyboardButton("Покормить/Откормить").callbackData("feed_fatten");
            feedHappy = new ArrayList<>();
            feedHappy.add(feedCapybara);
        }

        if (Boolean.TRUE.equals(capybara.canHappy())) {
            InlineKeyboardButton makeHappy = new InlineKeyboardButton("Осчастливить капибару").callbackData("make_happy");
            if (feedHappy == null)
                feedHappy = new ArrayList<>();
            feedHappy.add(makeHappy);
        }

        if (feedHappy != null) {
            rows.add(feedHappy);
        }

//        List<InlineKeyboardButton> bigJobs = null;
//
//        if (capybara.hasWork() && capybara.getCapybaraBigJob().getNextJob() <= date
//                && capybara.getCapybaraPreparation().getPrepared() == 1 && capybara.getCapybaraBigJob().getLevel() == 0) {
//            InlineKeyboardButton bigJob = new InlineKeyboardButton("Большое дело").callbackData("big_job");
//            bigJobs = new ArrayList<>();
//            bigJobs.add(bigJob);
//        } else if (capybara.hasWork() && capybara.getCapybaraPreparation().getPrepared() == 0 && capybara.getCapybaraPreparation().getOnJob() == 0
//                && capybara.getCapybaraBigJob().getNextJob() <= date && capybara.getLevel() >= 20
//                && capybara.getJob().getJobTimer().getLevel() != 1) {
//            InlineKeyboardButton bigJob = new InlineKeyboardButton("Подготовка к большому делу").callbackData("big_job_preparation");
//            bigJobs = new ArrayList<>();
//            bigJobs.add(bigJob);
//        } else if (capybara.hasWork() && capybara.getCapybaraBigJob().getTimeRemaining() <= date
//                && capybara.getCapybaraPreparation().getPrepared() == 1 && capybara.getCapybaraBigJob().getLevel() == 1) {
//            InlineKeyboardButton bigJob = new InlineKeyboardButton("Завершить большое дело").callbackData("end_big_job");
//            bigJobs = new ArrayList<>();
//            bigJobs.add(bigJob);
//        }
//
//
//        if (bigJobs != null) {
//            rows.add(bigJobs);
//        }
        List<InlineKeyboardButton> jobs = null;

        if (Boolean.TRUE.equals(capybara.canTakeFromWork())) {
            InlineKeyboardButton job = new InlineKeyboardButton("Забрать с работы").callbackData("take_from_job");
            jobs = new ArrayList<>();
            jobs.add(job);
        } else if (Boolean.FALSE.equals(capybara.hasWork())) {
            InlineKeyboardButton getJob = new InlineKeyboardButton("Устроиться на работу").callbackData("get_job");
            jobs = new ArrayList<>();
            jobs.add(getJob);
        }

        if (jobs != null) {
            rows.add(jobs);
        }

        List<InlineKeyboardButton> infoBtn = new ArrayList<>();

        InlineKeyboardButton info = new InlineKeyboardButton("Инфо").callbackData("info");
        infoBtn.add(info);

        rows.add(infoBtn);

        return new InlineKeyboardMarkup(rows.stream().map(row -> row.toArray(InlineKeyboardButton[]::new)).toArray(InlineKeyboardButton[][]::new));
    }

    public InlineKeyboardMarkup fightKeyboard() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton[]{
                        new InlineKeyboardButton("Атака").callbackData("fight_action_ATTACK")
                },
                new InlineKeyboardButton[]{
                        new InlineKeyboardButton("Хил").callbackData("fight_action_HEAL")
                },
                new InlineKeyboardButton[]{
                        new InlineKeyboardButton("Защита").callbackData("fight_action_DEFEND")
                },
                new InlineKeyboardButton[]{
                        new InlineKeyboardButton("скип").callbackData("fight_action_SKIP")
                }
        );
    }

    public InlineKeyboardMarkup infoKeyboard(CapybaraInfoDto capybara) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> mainRow = new ArrayList<>();
        List<InlineKeyboardButton> tea;
        List<InlineKeyboardButton> job;
        List<InlineKeyboardButton> improve;
        List<InlineKeyboardButton> race;
        List<InlineKeyboardButton> fight;

        InlineKeyboardButton main = new InlineKeyboardButton("Моя капибара").callbackData("go_to_main");
        mainRow.add(main);
        rows.add(mainRow);

        if (Boolean.TRUE.equals(capybara.canTea())) {
            InlineKeyboardButton teaBtn = new InlineKeyboardButton("Пойти на чаепитие").callbackData("go_tea");
            tea = new ArrayList<>();
            tea.add(teaBtn);
            rows.add(tea);
        }

        if (Boolean.TRUE.equals(capybara.canGoWork())) {
            InlineKeyboardButton jobBtn = new InlineKeyboardButton("Отправить капибару на работу").callbackData("go_job");
            job = new ArrayList<>();
            job.add(jobBtn);
            rows.add(job);
        }

        if (capybara.improvement() != null) {
            InlineKeyboardButton improvementBtn = new InlineKeyboardButton("Купить улучшение для гонок").callbackData("buy_improve");
            improve = new ArrayList<>();
            improve.add(improvementBtn);
            rows.add(improve);
        }

        if (Boolean.TRUE.equals(capybara.canRace())) {
            InlineKeyboardButton raceBtn = new InlineKeyboardButton("Забег").callbackData("start_race");
            race = new ArrayList<>();
            race.add(raceBtn);
            rows.add(race);
        }

        InlineKeyboardButton fightBtn = new InlineKeyboardButton("Бой с боссом").callbackData("fight_info");
        fight = List.of(fightBtn);
        rows.add(fight);

        InlineKeyboardButton[][] newRows = rows.stream()
                .map(row -> row.toArray(InlineKeyboardButton[]::new))
                .toArray(InlineKeyboardButton[][]::new);

        return new InlineKeyboardMarkup(newRows);
    }

    public InlineKeyboardMarkup improvements() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("Удобные ботиночки").callbackData("improve_boots")},
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("Вкусный арбуз").callbackData("improve_watermelon")},
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("Антипроигрыш").callbackData("improve_pill")}
        );
    }

    public InlineKeyboardMarkup teaKeyboard() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton("Забрать капибару с чаепития").callbackData("take_from_tea")
        );
    }

    public InlineKeyboardMarkup deleteCapybaraKeyboard() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton("Точно выкинуть капибару").callbackData("exactly_delete")
        );
    }

    public InlineKeyboardMarkup weddingKeyboard() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("Принять брак").callbackData("accept_wedding")},
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("Отказаться").callbackData("refuse_wedding")}
        );
    }

    public InlineKeyboardMarkup unWeddingKeyboard() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("Забрать свои слова назад").callbackData("refuse_wedding")},
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("Подтвердить расторжение").callbackData("un_wedding")}
        );
    }

    public InlineKeyboardMarkup raceKeyboard() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("Принять забег").callbackData("accept_race")},
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("Отказаться от забега").callbackData("refuse_race")}
        );
    }

    public InlineKeyboardMarkup raceMassage() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton("Сделать массаж").callbackData("do_massage")
        );
    }

    public InlineKeyboardMarkup notChange() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton("Не менять ничего").callbackData("not_change")
        );
    }

    public InlineKeyboardMarkup defaultPhoto() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("Не менять ничего").callbackData("not_change")},
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("Выбрать случайное фото").callbackData("set_default_photo")}
        );
    }

    public InlineKeyboardMarkup newJob() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("Программист\uD83D\uDC68\u200D\uD83D\uDCBB").callbackData("set_job_PROGRAMMING")},
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("Грабитель\uD83E\uDD77").callbackData("set_job_CRIMINAL")},
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("Кассир\uD83D\uDCB5").callbackData("set_job_CASHIER")},
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("Моя капибара").callbackData("go_to_main")}
        );
    }

    public InlineKeyboardMarkup robberyImprovement() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("\uD83E\uDD7EУдобные ботиночки").callbackData("big_job_boots")},
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("\uD83D\uDE97Быстрая машина").callbackData("big_job_car")},
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("💰Мешок для денег").callbackData("big_job_bag")},
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("\uD83D\uDC4C\uD83C\uDFFBНичего").callbackData("big_job_nothing")}
        );
    }

    public InlineKeyboardMarkup cashReportImprovement() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("☕Банка кофе").callbackData("big_job_coffee")},
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("\uD83D\uDDA8Принтер").callbackData("big_job_printer")},
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("💰Мешок для денег").callbackData("big_job_bag")},
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("\uD83D\uDC4C\uD83C\uDFFBНичего").callbackData("big_job_nothing")}
        );
    }

    public InlineKeyboardMarkup bigItProject() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("☕Банка кофе").callbackData("big_job_coffee")},
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("\uD83D\uDCDAКурсы по программированию").callbackData("big_job_courses")},
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("💰Мешок для денег").callbackData("big_job_bag")},
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("\uD83D\uDC4C\uD83C\uDFFBНичего").callbackData("big_job_nothing")}
        );
    }

    public InlineKeyboardMarkup bigJobKeyboard() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton("Большое дело").callbackData("big_job")
        );
    }

    public InlineKeyboardMarkup feedKeyboard() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("Покормить капибару").callbackData("feed")},
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("Откормить капибару").callbackData("fatten")},
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("Не делать ничего").callbackData("go_to_main")}
        );
    }

    public InlineKeyboardMarkup casinoTargetKeyboard() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("Красное").callbackData("casino_RED")},
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("Черное").callbackData("casino_BLACK")},
                new InlineKeyboardButton[]
                        {new InlineKeyboardButton("Зеро").callbackData("casino_ZERO")}
        );
    }

    public Keyboard takeCapybara() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton("Взять капибару").callbackData("take_capybara")
        );
    }

    public InlineKeyboardMarkup toMainMenu() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton("Моя капибара").callbackData("go_to_main")
        );
    }

    public InlineKeyboardMarkup fightInfoKeyboard(FightCapybaraDto fightCapybaraDto, Long chatId) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        if (fightCapybaraDto.canFight()) {
            rows.add(List.of(new InlineKeyboardButton("Присоединиться к сражению").callbackData("join_fight")));
        }
        if (bossFightProvider.canStartFight(chatId)) {
            rows.add(List.of(new InlineKeyboardButton("Начать сражение").callbackData("start_fight")));
        }
        rows.add(List.of(new InlineKeyboardButton("Купить ништяки").callbackData("list_of_buffs")));
        rows.add(List.of(new InlineKeyboardButton("Ничего").callbackData("go_to_main")));

        InlineKeyboardButton[][] newRows = rows.stream()
                .map(row -> row.toArray(InlineKeyboardButton[]::new))
                .toArray(InlineKeyboardButton[][]::new);

        return new InlineKeyboardMarkup(newRows);
    }

    public InlineKeyboardMarkup fightBuffTypes() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton[]{
                        new InlineKeyboardButton("Атака").callbackData("fight_buffs_ATTACK")
                },
                new InlineKeyboardButton[]{
                        new InlineKeyboardButton("Защита").callbackData("fight_buffs_DEFEND")
                },
                new InlineKeyboardButton[]{
                        new InlineKeyboardButton("Хил").callbackData("fight_buffs_HEAL")
                },
                new InlineKeyboardButton[]{
                        new InlineKeyboardButton("Ничего").callbackData("go_to_main")
                }
        );
    }

    public InlineKeyboardMarkup fightBuffs(BuffType buffType) {
        List<List<InlineKeyboardButton>> buffs = new ArrayList<>(switch (buffType) {
            case ATTACK -> getBuffs(FightBuffWeapon.values());
            case DEFEND -> getBuffs(FightBuffShield.values());
            case HEAL -> getBuffs(FightBuffHeal.values());
            case SPECIAL -> getBuffs(FightBuffSpecial.values());
        });
        buffs.add(List.of(new InlineKeyboardButton("Ничего").callbackData("go_to_main")));

        InlineKeyboardButton[][] newRows = buffs.stream()
                .map(row -> row.toArray(InlineKeyboardButton[]::new))
                .toArray(InlineKeyboardButton[][]::new);

        return new InlineKeyboardMarkup(newRows);
    }

    private List<List<InlineKeyboardButton>> getBuffs(FightBuffEnum[] values) {
        return Arrays.stream(values)
                .map(v -> List.of(new InlineKeyboardButton(v.getTitle()).callbackData("buy_buff_" + v.name() + "_" + v.getBuffType())))
                .toList();
    }

    public InlineKeyboardMarkup leaveFight() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton[]{
                        new InlineKeyboardButton("Ливнуть с позором").callbackData("leave_fight")
                },
                new InlineKeyboardButton[]{
                        new InlineKeyboardButton("Начать файт").callbackData("maybe_start_fight")
                },
                new InlineKeyboardButton[]{
                        new InlineKeyboardButton("Назад").callbackData("go_to_main")
                }
        );
    }

    public InlineKeyboardMarkup maybeStartFight() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton[]{
                        new InlineKeyboardButton("Да начинаем").callbackData("start_fight")
                },
                new InlineKeyboardButton[]{
                        new InlineKeyboardButton("Нет я ссу босса").callbackData("go_to_main")
                }
        );
    }
}
