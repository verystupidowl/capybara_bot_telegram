package ru.tggc.capibaraBotTelegram.keyboard;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import ru.tggc.capibaraBotTelegram.capybara.Capybara;

import java.util.ArrayList;
import java.util.List;

public class InlineKeyboardCreator {

    public InlineKeyboardMarkup myCapybaraKeyboard(Capybara capybara, int date) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> change = new ArrayList<>();
        InlineKeyboardButton setName = new InlineKeyboardButton("Изменить имя").callbackData("set_name");
        change.add(setName);
        InlineKeyboardButton setPhoto = new InlineKeyboardButton("Изменить фото").callbackData("set_photo");
        change.add(setPhoto);

        rows.add(change);
        List<InlineKeyboardButton> feedHappy = null;

        if (capybara.getSatiety().getTimeRemaining() <= date) {
            InlineKeyboardButton feedCapybara = new InlineKeyboardButton("Покормить/Откормить").callbackData("feed_fatten");
            feedHappy = new ArrayList<>();
            feedHappy.add(feedCapybara);
        }

        if (capybara.getHappiness().getTimeRemaining() <= date) {
            InlineKeyboardButton makeHappy = new InlineKeyboardButton("Осчастливить капибару").callbackData("make_happy");
            if (feedHappy == null)
                feedHappy = new ArrayList<>();
            feedHappy.add(makeHappy);
        }

        if (feedHappy != null) {
            rows.add(feedHappy);
        }

        List<InlineKeyboardButton> bigJobs = null;

        if (capybara.hasWork() && capybara.getCapybaraBigJob().getNextJob() <= date
                && capybara.getCapybaraPreparation().getPrepared() == 1 && capybara.getCapybaraBigJob().getLevel() == 0) {
            InlineKeyboardButton bigJob = new InlineKeyboardButton("Большое дело").callbackData("big_job");
            bigJobs = new ArrayList<>();
            bigJobs.add(bigJob);
        } else if (capybara.hasWork() && capybara.getCapybaraPreparation().getPrepared() == 0 && capybara.getCapybaraPreparation().getOnJob() == 0
                && capybara.getCapybaraBigJob().getNextJob() <= date && capybara.getLevel() >= 20
                && capybara.getJob().getJobTimer().getLevel() != 1) {
            InlineKeyboardButton bigJob = new InlineKeyboardButton("Подготовка к большому делу").callbackData("big_job_preparation");
            bigJobs = new ArrayList<>();
            bigJobs.add(bigJob);
        } else if (capybara.hasWork() && capybara.getCapybaraBigJob().getTimeRemaining() <= date
                && capybara.getCapybaraPreparation().getPrepared() == 1 && capybara.getCapybaraBigJob().getLevel() == 1) {
            InlineKeyboardButton bigJob = new InlineKeyboardButton("Завершить большое дело").callbackData("end_big_job");
            bigJobs = new ArrayList<>();
            bigJobs.add(bigJob);
        }


        if (bigJobs != null) {
            rows.add(bigJobs);
        }
        List<InlineKeyboardButton> jobs = null;

        if (capybara.hasWork()) {
            if (capybara.getJob().getJobTimer().getTimeRemaining() <= date && capybara.getJob().getJobTimer().getLevel() == 1) {
                InlineKeyboardButton job = new InlineKeyboardButton("Забрать с работы").callbackData("take_from_job");
                jobs = new ArrayList<>();
                jobs.add(job);
            }
        } else {
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

    public InlineKeyboardMarkup infoKeyboard(Capybara capybara, int date) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> mainRow = new ArrayList<>();
        List<InlineKeyboardButton> tea;
        List<InlineKeyboardButton> job;
        List<InlineKeyboardButton> improve;

        InlineKeyboardButton main = new InlineKeyboardButton("Моя капибара").callbackData("go_to_main");
        mainRow.add(main);
        rows.add(mainRow);

        if (capybara.getTea().getTimeRemaining() <= date && capybara.getTea().getLevel() == 0) {
            InlineKeyboardButton teaBtn = new InlineKeyboardButton("Пойти на чаепитие").callbackData("go_tea");
            tea = new ArrayList<>();
            tea.add(teaBtn);
            rows.add(tea);
        }

        if (capybara.hasWork() && capybara.getJob().getJobTimer().getNextJob() <= date
                && capybara.getJob().getJobTimer().getLevel() == 0 && capybara.getCapybaraBigJob().getLevel() != 1) {
            InlineKeyboardButton jobBtn = new InlineKeyboardButton("Отправить капибару на работу").callbackData("go_job");
            job = new ArrayList<>();
            job.add(jobBtn);
            rows.add(job);
        }

        if (capybara.getImprovement().getLevel() == 0) {
            InlineKeyboardButton improvementBtn = new InlineKeyboardButton("Купить улучшение для гонок").callbackData("buy_improve");
            improve = new ArrayList<>();
            improve.add(improvementBtn);
            rows.add(improve);
        }

        InlineKeyboardButton[][] newRows = rows.stream().map(row -> row.toArray(InlineKeyboardButton[]::new)).toArray(InlineKeyboardButton[][]::new);

        return new InlineKeyboardMarkup(newRows);
    }

    public InlineKeyboardMarkup improvements() {
        return new InlineKeyboardMarkup(new InlineKeyboardButton[][]{
                {new InlineKeyboardButton("Удобные ботиночки").callbackData("improve_boots")},
                {new InlineKeyboardButton("Вкусный арбуз").callbackData("improve_watermelon")},
                {new InlineKeyboardButton("Антипроигрыш").callbackData("improve_pill")}
        });
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
        return new InlineKeyboardMarkup(new InlineKeyboardButton[][]{
                {new InlineKeyboardButton("Принять брак").callbackData("accept_wedding")},
                {new InlineKeyboardButton("Отказаться").callbackData("refuse_wedding")}
        });
    }

    public InlineKeyboardMarkup unWeddingKeyboard() {
        return new InlineKeyboardMarkup(new InlineKeyboardButton[][]{
                {new InlineKeyboardButton("Забрать свои слова назад").callbackData("refuse_wedding")},
                {new InlineKeyboardButton("Подтвердить расторжение").callbackData("un_wedding")},
        });
    }

    public InlineKeyboardMarkup raceKeyboard() {
        return new InlineKeyboardMarkup(new InlineKeyboardButton[][]{
                {new InlineKeyboardButton("Принять забег").callbackData("accept_race")},
                {new InlineKeyboardButton("Отказаться от забега").callbackData("refuse_race")}
        });
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
        return new InlineKeyboardMarkup(new InlineKeyboardButton[][]{
                {new InlineKeyboardButton("Не менять ничего").callbackData("not_change")},
                {new InlineKeyboardButton("Выбрать случайное фото").callbackData("set_default_photo")}
        });
    }

    public InlineKeyboardMarkup newJob() {
        return new InlineKeyboardMarkup(new InlineKeyboardButton[][]{
                {new InlineKeyboardButton("Программист\uD83D\uDC68\u200D\uD83D\uDCBB").callbackData("prog_job")},
                {new InlineKeyboardButton("Грабитель\uD83E\uDD77").callbackData("crim_job")},
                {new InlineKeyboardButton("Кассир\uD83D\uDCB5").callbackData("cash_job")}
        });
    }

    public InlineKeyboardMarkup robberyImprovement() {
        return new InlineKeyboardMarkup(new InlineKeyboardButton[][]{
                {new InlineKeyboardButton("\uD83E\uDD7EУдобные ботиночки").callbackData("big_job_boots")},
                {new InlineKeyboardButton("\uD83D\uDE97Быстрая машина").callbackData("big_job_car")},
                {new InlineKeyboardButton("💰Мешок для денег").callbackData("big_job_bag")},
                {new InlineKeyboardButton("\uD83D\uDC4C\uD83C\uDFFBНичего").callbackData("big_job_nothing")}
        });
    }

    public InlineKeyboardMarkup cashReportImprovement() {
        return new InlineKeyboardMarkup(new InlineKeyboardButton[][]{
                {new InlineKeyboardButton("☕Банка кофе").callbackData("big_job_coffee")},
                {new InlineKeyboardButton("\uD83D\uDDA8Принтер").callbackData("big_job_printer")},
                {new InlineKeyboardButton("💰Мешок для денег").callbackData("big_job_bag")},
                {new InlineKeyboardButton("\uD83D\uDC4C\uD83C\uDFFBНичего").callbackData("big_job_nothing")}
        });
    }

    public InlineKeyboardMarkup bigItProject() {
        return new InlineKeyboardMarkup(new InlineKeyboardButton[][]{
                {new InlineKeyboardButton("☕Банка кофе").callbackData("big_job_coffee")},
                {new InlineKeyboardButton("\uD83D\uDCDAКурсы по программированию").callbackData("big_job_courses")},
                {new InlineKeyboardButton("💰Мешок для денег").callbackData("big_job_bag")},
                {new InlineKeyboardButton("\uD83D\uDC4C\uD83C\uDFFBНичего").callbackData("big_job_nothing")}
        });
    }

    public InlineKeyboardMarkup bigJobKeyboard() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton("Большое дело").callbackData("big_job")
        );
    }

    public InlineKeyboardMarkup feedKeyboard() {
        return new InlineKeyboardMarkup(new InlineKeyboardButton[][]{
                {new InlineKeyboardButton("Покормить капибару").callbackData("feed")},
                {new InlineKeyboardButton("Откормить капибару").callbackData("fatten")}
        });
    }
}
