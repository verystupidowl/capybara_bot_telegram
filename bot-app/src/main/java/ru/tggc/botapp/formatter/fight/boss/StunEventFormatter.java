package ru.tggc.botapp.formatter.fight.boss;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tggc.botapp.fight.event.boss.StunEvent;
import ru.tggc.botapp.formatter.fight.FightEventFormatter;
import ru.tggc.botapp.formatter.msgkey.FightMsgKey;
import ru.tggc.telegrambotcore.formatter.FormatService;

@Service
@RequiredArgsConstructor
public class StunEventFormatter implements FightEventFormatter<StunEvent> {
    private final FormatService formatService;

    @Override
    public Class<StunEvent> type() {
        return StunEvent.class;
    }

    @Override
    public String format(StunEvent event) {
        return formatService.get(FightMsgKey.BOSS_STUN, event.username(), event.damage());
    }
}
