package ru.tggc.botapp.formatter.fight;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tggc.botapp.fight.event.DamageDealtEvent;
import ru.tggc.botapp.formatter.msgkey.FightMsgKey;
import ru.tggc.telegrambotcore.formatter.FormatService;

@Component
@RequiredArgsConstructor
public class DamageDealtEventFormatter implements FightEventFormatter<DamageDealtEvent> {
    private final FormatService formatService;

    @Override
    public Class<DamageDealtEvent> type() {
        return DamageDealtEvent.class;
    }

    @Override
    public String format(DamageDealtEvent event) {
        return formatService.random(FightMsgKey.PLAYER_ATTACK, event.username(), event.damage());
    }
}
