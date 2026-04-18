package ru.tggc.capybaratelegrambot.keyboard.impls.fight;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.domain.dto.FightCapybaraDto;
import ru.tggc.capybaratelegrambot.keyboard.AbstractInlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.provider.BossFightProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static ru.tggc.capybaratelegrambot.keyboard.KeyboardKey.FIGHT_INFO;

@Component
public class FightInfoKeyboard extends AbstractInlineKeyboardCreator<FightCapybaraDto> {
    private final BossFightProvider bossFightProvider;

    public FightInfoKeyboard(BossFightProvider bossFightProvider) {
        super(FIGHT_INFO);
        this.bossFightProvider = bossFightProvider;
    }

    @Override
    public Function<FightCapybaraDto, List<List<InlineKeyboardButton>>> getRowsFunction() {
        return fightCapybaraDto -> {
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            if (fightCapybaraDto.canFight()) {
                rows.add(List.of(btn("Присоединиться к сражению", "join_fight")));
            }
            if (bossFightProvider.canStartFight(fightCapybaraDto.chatId())) {
                rows.add(List.of(btn("Начать сражение", "start_fight")));
            }
            rows.add(List.of(btn("Изменить класс капибары", "start_change_class")));
            rows.add(List.of(btn("Купить ништяки", "list_of_buffs")));
            rows.add(List.of(btn("Ничего", "go_to_main")));

            return rows;
        };
    }
}
