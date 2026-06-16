package ru.tggc.botapp.keyboard.impls.common;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.botapp.domain.dto.MyCapybaraDto;
import ru.tggc.botapp.keyboard.AbstractInlineKeyboardCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static ru.tggc.botapp.keyboard.KeyboardKey.MY_CAPYBARA;

@Component
public class MyCapybaraKeyboard extends AbstractInlineKeyboardCreator<MyCapybaraDto> {

    public MyCapybaraKeyboard() {
        super(MY_CAPYBARA);
    }

    @Override
    public Function<MyCapybaraDto, List<List<InlineKeyboardButton>>> getRowsFunction() {
        return capybara -> {
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            List<InlineKeyboardButton> change = new ArrayList<>();
            InlineKeyboardButton setName = btn("Изменить имя", "set_name");
            change.add(setName);
            InlineKeyboardButton setPhoto = btn("Изменить фото", "set_photo");
            change.add(setPhoto);

            rows.add(change);
            List<InlineKeyboardButton> feedHappy = null;

            if (Boolean.TRUE.equals(capybara.canSatiety())) {
                InlineKeyboardButton feedCapybara = btn("Покормить/Откормить", "feed_fatten");
                feedHappy = new ArrayList<>();
                feedHappy.add(feedCapybara);
            }

            if (Boolean.TRUE.equals(capybara.canHappy())) {
                InlineKeyboardButton makeHappy = btn("Осчастливить капибару", "make_happy");
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
                InlineKeyboardButton job = btn("Забрать с работы", "take_from_job");
                jobs = new ArrayList<>();
                jobs.add(job);
            } else if (Boolean.FALSE.equals(capybara.hasWork())) {
                InlineKeyboardButton getJob = btn("Устроиться на работу", "get_job");
                jobs = new ArrayList<>();
                jobs.add(getJob);
            }

            if (jobs != null) {
                rows.add(jobs);
            }

            List<InlineKeyboardButton> infoBtn = new ArrayList<>();

            InlineKeyboardButton info = btn("Инфо", "info");
            infoBtn.add(info);

            rows.add(infoBtn);

            return rows;
        };
    }
}
