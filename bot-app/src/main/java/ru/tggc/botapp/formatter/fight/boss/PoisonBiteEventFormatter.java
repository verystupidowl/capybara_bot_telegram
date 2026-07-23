package ru.tggc.botapp.formatter.fight.boss;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tggc.botapp.fight.event.boss.PoisonBiteEvent;
import ru.tggc.botapp.formatter.fight.FightEventFormatter;
import ru.tggc.botapp.formatter.msgkey.FightMsgKey;
import ru.tggc.telegrambotcore.formatter.FormatService;

@Service
@RequiredArgsConstructor
public class PoisonBiteEventFormatter implements FightEventFormatter<PoisonBiteEvent> {

    private final FormatService formatService;

    @Override
    public Class<PoisonBiteEvent> type() {
        return PoisonBiteEvent.class;
    }

    @Override
    public String format(PoisonBiteEvent event) {
        return formatService.get(FightMsgKey.BOSS_POISON_BITE, event.username(), event.damage());
    }
}
