package ru.tggc.botapp.formatter.fight;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tggc.botapp.fight.event.DefendEvent;
import ru.tggc.botapp.formatter.msgkey.FightMsgKey;
import ru.tggc.telegrambotcore.formatter.FormatService;

@Component
@RequiredArgsConstructor
public class DefendEventFormatter implements FightEventFormatter<DefendEvent> {
    private final FormatService formatService;

    @Override
    public Class<DefendEvent> type() {
        return DefendEvent.class;
    }

    @Override
    public String format(DefendEvent event) {
        return formatService.random(FightMsgKey.PLAYER_DEFEND, event.username());
    }
}
