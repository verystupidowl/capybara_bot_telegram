package ru.tggc.botapp.keyboard.impls.common;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.botapp.domain.dto.info.CapybaraInfoDto;
import ru.tggc.telegrambotcore.keyboard.AbstractInlineKeyboardCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static ru.tggc.botapp.keyboard.KeyboardType.INFO;
import static ru.tggc.botapp.util.KeyboardUtils.toMainMenuBtn;

@Component
public class InfoKeyboard extends AbstractInlineKeyboardCreator<CapybaraInfoDto> {

    public InfoKeyboard() {
        super(INFO);
    }

    @Override
    public Function<CapybaraInfoDto, List<List<InlineKeyboardButton>>> getRowsFunction() {
        return capybara -> {
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            List<InlineKeyboardButton> mainRow = List.of(toMainMenuBtn("Моя капибара"));
            rows.add(mainRow);

            if (capybara.tea().isCanAct() && !capybara.tea().isWaiting()) {
                InlineKeyboardButton teaBtn = btn("Пойти на чаепитие", "go_tea");
                List<InlineKeyboardButton> tea = List.of(teaBtn);
                rows.add(tea);
            }

            if (capybara.work().isCanAct()) {
                InlineKeyboardButton jobBtn = btn("Отправить капибару на работу", "go_job");
                List<InlineKeyboardButton> job = List.of(jobBtn);
                rows.add(job);
            }

            if (capybara.race().getImprovement().equals("Ничего")) {
                InlineKeyboardButton improvementBtn = btn("Купить улучшение для гонок", "buy_improve");
                List<InlineKeyboardButton> improve = List.of(improvementBtn);
                rows.add(improve);
            }

            if (capybara.race().isCanAct()) {
                InlineKeyboardButton raceBtn = btn("Забег", "start_race");
                List<InlineKeyboardButton> race = List.of(raceBtn);
                rows.add(race);
            }

            if (capybara.weddingGift() != null && capybara.weddingGift().isCanAct()) {
                InlineKeyboardButton weddingGiftBtn = btn("Подарок", "wedding_gift");
                List<InlineKeyboardButton> weddingGift = List.of(weddingGiftBtn);
                rows.add(weddingGift);
            }

            InlineKeyboardButton fightBtn = btn("Бой с боссом", "fight_info");
            List<InlineKeyboardButton> fight = List.of(fightBtn);
            rows.add(fight);

            return rows;
        };
    }
}
