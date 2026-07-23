package ru.tggc.botapp.formatter.fight.boss;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tggc.botapp.fight.event.boss.BossCriticalHitEvent;
import ru.tggc.botapp.formatter.fight.FightEventFormatter;
import ru.tggc.botapp.formatter.msgkey.FightMsgKey;
import ru.tggc.telegrambotcore.formatter.FormatService;

@Service
@RequiredArgsConstructor
public class BossCriticalHitEventFormatter implements FightEventFormatter<BossCriticalHitEvent> {

    private final FormatService formatService;

    @Override
    public Class<BossCriticalHitEvent> type() {
        return BossCriticalHitEvent.class;
    }

    @Override
    public String format(BossCriticalHitEvent event) {
        return formatService.get(FightMsgKey.BOSS_CRITICAL_HIT);
    }
}
