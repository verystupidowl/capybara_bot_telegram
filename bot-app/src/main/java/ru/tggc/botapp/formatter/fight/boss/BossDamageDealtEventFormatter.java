package ru.tggc.botapp.formatter.fight.boss;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tggc.botapp.fight.event.boss.DamageDealtEvent;
import ru.tggc.botapp.formatter.fight.FightEventFormatter;
import ru.tggc.botapp.formatter.msgkey.FightMsgKey;
import ru.tggc.telegrambotcore.formatter.FormatService;

@Service
@RequiredArgsConstructor
public class BossDamageDealtEventFormatter implements FightEventFormatter<DamageDealtEvent> {
    private final FormatService formatService;

    @Override
    public Class<DamageDealtEvent> type() {
        return DamageDealtEvent.class;
    }

    @Override
    public String format(DamageDealtEvent event) {
        return formatService.get(FightMsgKey.BOSS_DAMAGE_DEALT, event.username(), event.damage());
    }
}
