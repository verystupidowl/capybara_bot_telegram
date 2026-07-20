package ru.tggc.botapp.keyboard.impls.common;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.botapp.domain.dto.info.CapybaraInfoDto;
import ru.tggc.botapp.keyboard.AbstractInlineKeyboardCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static ru.tggc.botapp.keyboard.KeyboardKey.INFO;

@Component
public class InfoKeyboardCreator extends AbstractInlineKeyboardCreator<CapybaraInfoDto> {

    public InfoKeyboardCreator() {
        super(INFO);
    }

    @Override
    public Function<CapybaraInfoDto, List<List<InlineKeyboardButton>>> getRowsFunction() {
        return capybara -> {
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            List<InlineKeyboardButton> mainRow = new ArrayList<>();
            List<InlineKeyboardButton> tea;
            List<InlineKeyboardButton> job;
            List<InlineKeyboardButton> improve;
            List<InlineKeyboardButton> race;
            List<InlineKeyboardButton> fight;

            InlineKeyboardButton main = toMainMenuBtn("Моя капибара");
            mainRow.add(main);
            rows.add(mainRow);

            if (capybara.tea().isCanAct()) {
                InlineKeyboardButton teaBtn = btn("Пойти на чаепитие", "go_tea");
                tea = new ArrayList<>();
                tea.add(teaBtn);
                rows.add(tea);
            }

            if (capybara.work().isCanAct()) {
                InlineKeyboardButton jobBtn = btn("Отправить капибару на работу", "go_job");
                job = new ArrayList<>();
                job.add(jobBtn);
                rows.add(job);
            }

            if (capybara.race().getImprovement() != null) {
                InlineKeyboardButton improvementBtn = btn("Купить улучшение для гонок", "buy_improve");
                improve = new ArrayList<>();
                improve.add(improvementBtn);
                rows.add(improve);
            }

            if (capybara.race().isCanAct()) {
                InlineKeyboardButton raceBtn = btn("Забег", "start_race");
                race = new ArrayList<>();
                race.add(raceBtn);
                rows.add(race);
            }

            InlineKeyboardButton fightBtn = btn("Бой с боссом", "fight_info");
            fight = List.of(fightBtn);
            rows.add(fight);

            return rows;
        };
    }
}
