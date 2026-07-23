package ru.tggc.botapp.formatter.fight.boss;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tggc.botapp.fight.event.boss.AoeDamageEvent;
import ru.tggc.botapp.formatter.fight.FightEventFormatter;
import ru.tggc.botapp.formatter.msgkey.FightMsgKey;
import ru.tggc.telegrambotcore.formatter.FormatService;

@Service
@RequiredArgsConstructor
public class AoeDamageEventFormatter implements FightEventFormatter<AoeDamageEvent> {
    private final FormatService formatService;

    @Override
    public Class<AoeDamageEvent> type() {
        return AoeDamageEvent.class;
    }

    @Override
    public String format(AoeDamageEvent event) {
        return formatService.get(FightMsgKey.BOSS_AOE_DAMAGE);
    }
}
