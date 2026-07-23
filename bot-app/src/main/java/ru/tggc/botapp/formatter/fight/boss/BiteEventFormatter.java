package ru.tggc.botapp.formatter.fight.boss;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tggc.botapp.fight.event.boss.BiteEvent;
import ru.tggc.botapp.formatter.fight.FightEventFormatter;
import ru.tggc.botapp.formatter.msgkey.FightMsgKey;
import ru.tggc.telegrambotcore.formatter.FormatService;

@Service
@RequiredArgsConstructor
public class BiteEventFormatter implements FightEventFormatter<BiteEvent> {
    private final FormatService formatService;

    @Override
    public Class<BiteEvent> type() {
        return BiteEvent.class;
    }

    @Override
    public String format(BiteEvent event) {
        return formatService.get(FightMsgKey.BOSS_BITE, event.username(), event.damage());
    }
}
