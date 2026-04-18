package ru.tggc.capybaratelegrambot.keyboard.impls.common;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraInfoDto;
import ru.tggc.capybaratelegrambot.keyboard.AbstractInlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.keyboard.KeyboardType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
public class InfoKeyboardCreator extends AbstractInlineKeyboardCreator<CapybaraInfoDto> {

    public InfoKeyboardCreator() {
        super(KeyboardType.INFO);
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

            if (Boolean.TRUE.equals(capybara.canTea())) {
                InlineKeyboardButton teaBtn = btn("Пойти на чаепитие", "go_tea");
                tea = new ArrayList<>();
                tea.add(teaBtn);
                rows.add(tea);
            }

            if (Boolean.TRUE.equals(capybara.canGoWork())) {
                InlineKeyboardButton jobBtn = btn("Отправить капибару на работу", "go_job");
                job = new ArrayList<>();
                job.add(jobBtn);
                rows.add(job);
            }

            if (capybara.improvement() != null) {
                InlineKeyboardButton improvementBtn = btn("Купить улучшение для гонок", "buy_improve");
                improve = new ArrayList<>();
                improve.add(improvementBtn);
                rows.add(improve);
            }

            if (Boolean.TRUE.equals(capybara.canRace())) {
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
