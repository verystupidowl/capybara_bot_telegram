package ru.tggc.botapp.formatter.fight;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tggc.botapp.fight.event.CriticalHitEvent;
import ru.tggc.botapp.formatter.msgkey.FightMsgKey;
import ru.tggc.telegrambotcore.formatter.FormatService;

@Component
@RequiredArgsConstructor
public class CriticalHitEventFormatter implements FightEventFormatter<CriticalHitEvent> {
    private final FormatService formatService;

    @Override
    public Class<CriticalHitEvent> type() {
        return CriticalHitEvent.class;
    }

    @Override
    public String format(CriticalHitEvent event) {
        return formatService.get(FightMsgKey.PLAYER_CRITICAL_HIT,  event.username());
    }
}
