package ru.tggc.capybaratelegrambot.keyboard.impls.fight;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.domain.model.enums.fight.BuffType;
import ru.tggc.capybaratelegrambot.domain.model.enums.fight.FightBuffEnum;
import ru.tggc.capybaratelegrambot.domain.model.enums.fight.FightBuffHeal;
import ru.tggc.capybaratelegrambot.domain.model.enums.fight.FightBuffShield;
import ru.tggc.capybaratelegrambot.domain.model.enums.fight.FightBuffSpecial;
import ru.tggc.capybaratelegrambot.domain.model.enums.fight.FightBuffWeapon;
import ru.tggc.capybaratelegrambot.keyboard.AbstractInlineKeyboardCreator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static ru.tggc.capybaratelegrambot.keyboard.KeyboardType.FIGHT_BUFFS;

@Component
public class FightBuffsKeyboard extends AbstractInlineKeyboardCreator<BuffType> {

    public FightBuffsKeyboard() {
        super(FIGHT_BUFFS);
    }

    @Override
    public Function<BuffType, List<List<InlineKeyboardButton>>> getRowsFunction() {
        return buffType -> {
            List<List<InlineKeyboardButton>> buffs = new ArrayList<>(switch (buffType) {
                case ATTACK -> getBuffs(FightBuffWeapon.values());
                case DEFEND -> getBuffs(FightBuffShield.values());
                case HEAL -> getBuffs(FightBuffHeal.values());
                case SPECIAL -> getBuffs(FightBuffSpecial.values());
            });
            buffs.add(List.of(toMainMenuBtn("Ничего")));

            return buffs;
        };
    }

    private List<List<InlineKeyboardButton>> getBuffs(FightBuffEnum[] values) {
        return Arrays.stream(values)
                .map(v -> List.of(new InlineKeyboardButton(v.getTitle()).callbackData("buy_buff_" + v.name() + "_" + v.getBuffType())))
                .toList();
    }
}
