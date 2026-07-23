package ru.tggc.botapp.formatter.fight.boss;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tggc.botapp.fight.event.boss.FocusedStrikeEvent;
import ru.tggc.botapp.formatter.fight.FightEventFormatter;
import ru.tggc.botapp.formatter.msgkey.FightMsgKey;
import ru.tggc.telegrambotcore.formatter.FormatService;

@Service
@RequiredArgsConstructor
public class FocusedStrikeEventFormatter implements FightEventFormatter<FocusedStrikeEvent> {

    private final FormatService formatService;

    @Override
    public Class<FocusedStrikeEvent> type() {
        return FocusedStrikeEvent.class;
    }

    @Override
    public String format(FocusedStrikeEvent event) {
        return formatService.get(FightMsgKey.BOSS_FOCUSED_STRIKE, event.username(), event.damage());
    }
}
