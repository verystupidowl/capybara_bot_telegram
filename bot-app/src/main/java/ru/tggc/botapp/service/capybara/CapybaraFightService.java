package ru.tggc.botapp.service.capybara;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tggc.botapp.domain.dto.FightCapybaraDto;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.enums.fight.BuffType;
import ru.tggc.botapp.domain.model.enums.fight.FightBuffHeal;
import ru.tggc.botapp.domain.model.enums.fight.FightBuffShield;
import ru.tggc.botapp.domain.model.enums.fight.FightBuffSpecial;
import ru.tggc.botapp.domain.model.enums.fight.FightBuffWeapon;
import ru.tggc.botapp.exceptions.CapybaraException;
import ru.tggc.botapp.formatter.msgkey.ErrorMsgKey;
import ru.tggc.botapp.mapper.FightCapybaraMapper;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.formatter.FormatService;

import static ru.tggc.telegrambotcore.util.Utils.throwIf;

@Slf4j
@Service
@RequiredArgsConstructor
public class CapybaraFightService {
    private final CapybaraQueryService queryService;
    private final FormatService formatService;
    private final FightCapybaraMapper fightCapybaraMapper;

    public FightCapybaraDto getFightInfo(UpdateContext ctx) {
        Capybara fightCapybara = queryService.getFightCapybara(ctx);
        return fightCapybaraMapper.toDto(fightCapybara.getFight(), ctx.chatId());
    }

    public void buyBuff(UpdateContext ctx, String buff, BuffType buffType) {
        Capybara fightCapybara = queryService.getFightCapybara(ctx);
        switch (buffType) {
            case ATTACK -> {
                throwIf(
                        fightCapybara.getFight().getWeapon() != FightBuffWeapon.NONE,
                        () -> new CapybaraException(formatService.get(ErrorMsgKey.CAPYBARA_FIGHT_ONLY_ONE, "оружия"))
                );
                buyWeapon(fightCapybara, FightBuffWeapon.valueOf(buff));
            }
            case DEFEND -> {
                throwIf(
                        fightCapybara.getFight().getShield() != FightBuffShield.NONE,
                        () -> new CapybaraException(formatService.get(ErrorMsgKey.CAPYBARA_FIGHT_ONLY_ONE, "щита"))
                );
                buyShield(fightCapybara, FightBuffShield.valueOf(buff));
            }
            case HEAL -> {
                throwIf(
                        fightCapybara.getFight().getHeal() != FightBuffHeal.NONE,
                        () -> new CapybaraException(formatService.get(ErrorMsgKey.CAPYBARA_FIGHT_ONLY_ONE, "лечения"))
                );
                buyHeal(fightCapybara, FightBuffHeal.valueOf(buff));
            }
            case SPECIAL -> {
                throwIf(
                        fightCapybara.getFight().getSpecial() != FightBuffSpecial.NONE,
                        () -> new CapybaraException(formatService.get(ErrorMsgKey.CAPYBARA_FIGHT_ONLY_ONE, "спец. оружия"))
                );
                buySpecial(fightCapybara, FightBuffSpecial.valueOf(buff));
            }
        }
        queryService.save(fightCapybara);
    }


    private void buySpecial(Capybara fightCapybara, FightBuffSpecial fightBuffSpecial) {
        fightCapybara.decreaseMoney(fightBuffSpecial.getCost());
        fightCapybara.getFight().setSpecial(fightBuffSpecial);
    }

    private void buyHeal(Capybara fightCapybara, FightBuffHeal fightBuffHeal) {
        fightCapybara.decreaseMoney(fightBuffHeal.getCost());
        fightCapybara.getFight().setHeal(fightBuffHeal);
    }

    private void buyShield(Capybara fightCapybara, FightBuffShield fightBuffShield) {
        fightCapybara.decreaseMoney(fightBuffShield.getCost());
        fightCapybara.getFight().setShield(fightBuffShield);
    }

    private void buyWeapon(Capybara fightCapybara, FightBuffWeapon fightBuffWeapon) {
        fightCapybara.decreaseMoney(fightBuffWeapon.getCost());
        fightCapybara.getFight().setWeapon(fightBuffWeapon);
    }
}
