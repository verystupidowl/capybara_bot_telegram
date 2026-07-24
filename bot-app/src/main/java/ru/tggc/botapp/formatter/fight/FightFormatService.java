package ru.tggc.botapp.formatter.fight;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.tggc.botapp.domain.dto.FightCapybaraDto;
import ru.tggc.botapp.domain.model.enums.fight.BuffType;
import ru.tggc.botapp.domain.model.enums.fight.FightBuffEnum;
import ru.tggc.botapp.domain.model.enums.fight.FightBuffHeal;
import ru.tggc.botapp.domain.model.enums.fight.FightBuffShield;
import ru.tggc.botapp.domain.model.enums.fight.FightBuffSpecial;
import ru.tggc.botapp.domain.model.enums.fight.FightBuffWeapon;
import ru.tggc.botapp.formatter.msgkey.FightMsgKey;
import ru.tggc.telegrambotcore.formatter.FormatService;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FightFormatService {
    private final FormatService formatService;

    public String getFightInfo(FightCapybaraDto fightInfo) {
        String timeUntil = fightInfo.canFight() ? "Уже можно" : fightInfo.fightTime();
        String buffs = fightInfo.buffs().stream()
                .map(FightBuffEnum::getTitle)
                .reduce("", (a, b) -> a + b + "\n");
        return formatService.get(FightMsgKey.FIGHT_INFO, timeUntil, buffs);
    }


    public String getBuffs(BuffType buffType) {
        return switch (buffType) {
            case ATTACK -> getCollect(FightBuffWeapon.values());
            case DEFEND -> getCollect(FightBuffShield.values());
            case HEAL -> getCollect(FightBuffHeal.values());
            case SPECIAL -> getCollect(FightBuffSpecial.values());
        };
    }

    @NotNull
    private String getCollect(FightBuffEnum[] buff) {
        return Arrays.stream(buff)
                .map(v -> v.getTitle() + " - \uD83C\uDF49" + v.getCost() + "\n" + v.getDescription())
                .collect(Collectors.joining("\n\n"));
    }
}
