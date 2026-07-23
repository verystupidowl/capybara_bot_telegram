package ru.tggc.botapp.formatter.fight.boss;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tggc.botapp.fight.event.boss.HealEvent;
import ru.tggc.botapp.formatter.fight.FightEventFormatter;
import ru.tggc.botapp.formatter.msgkey.FightMsgKey;
import ru.tggc.telegrambotcore.formatter.FormatService;

@Service
@RequiredArgsConstructor
public class BossHealEventFormatter implements FightEventFormatter<HealEvent> {
    private final FormatService formatService;

    @Override
    public Class<HealEvent> type() {
        return HealEvent.class;
    }

    @Override
    public String format(HealEvent event) {
        return formatService.get(FightMsgKey.BOSS_HEAL, event.heal());
    }
}
