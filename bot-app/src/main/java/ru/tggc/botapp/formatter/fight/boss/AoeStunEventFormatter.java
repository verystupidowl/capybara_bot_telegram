package ru.tggc.botapp.formatter.fight.boss;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tggc.botapp.fight.event.boss.AoeStunEvent;
import ru.tggc.botapp.formatter.fight.FightEventFormatter;
import ru.tggc.botapp.formatter.msgkey.FightMsgKey;
import ru.tggc.telegrambotcore.formatter.FormatService;

@Service
@RequiredArgsConstructor
public class AoeStunEventFormatter implements FightEventFormatter<AoeStunEvent> {
    private final FormatService formatService;

    @Override
    public Class<AoeStunEvent> type() {
        return AoeStunEvent.class;
    }

    @Override
    public String format(AoeStunEvent event) {
        return formatService.get(FightMsgKey.BOSS_AOE_STUN);
    }
}
