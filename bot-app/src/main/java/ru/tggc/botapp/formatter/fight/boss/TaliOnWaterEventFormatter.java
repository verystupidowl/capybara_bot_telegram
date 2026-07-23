package ru.tggc.botapp.formatter.fight.boss;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tggc.botapp.fight.event.boss.TailOnTheWaterEvent;
import ru.tggc.botapp.formatter.fight.FightEventFormatter;
import ru.tggc.botapp.formatter.msgkey.FightMsgKey;
import ru.tggc.telegrambotcore.formatter.FormatService;

@Service
@RequiredArgsConstructor
public class TaliOnWaterEventFormatter implements FightEventFormatter<TailOnTheWaterEvent> {
    private final FormatService formatService;

    @Override
    public Class<TailOnTheWaterEvent> type() {
        return TailOnTheWaterEvent.class;
    }

    @Override
    public String format(TailOnTheWaterEvent event) {
        return formatService.get(FightMsgKey.BOSS_TAIL_ON_THE_WATER);
    }
}
