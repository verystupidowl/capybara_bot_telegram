package ru.tggc.botapp.formatter.fight.boss;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tggc.botapp.fight.event.boss.TailSlamDustEvent;
import ru.tggc.botapp.formatter.fight.FightEventFormatter;
import ru.tggc.botapp.formatter.msgkey.FightMsgKey;
import ru.tggc.telegrambotcore.formatter.FormatService;

@Service
@RequiredArgsConstructor
public class TailSlamDustEventFormatter implements FightEventFormatter<TailSlamDustEvent> {

    private final FormatService formatService;

    @Override
    public Class<TailSlamDustEvent> type() {
        return TailSlamDustEvent.class;
    }

    @Override
    public String format(TailSlamDustEvent event) {
        return formatService.get(FightMsgKey.BOSS_TAIL_SLAM_DUST, event.username(), event.damage(), event.turnsLeft());
    }
}
