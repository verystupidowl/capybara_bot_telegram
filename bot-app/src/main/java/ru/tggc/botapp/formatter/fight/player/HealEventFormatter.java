package ru.tggc.botapp.formatter.fight.player;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.tggc.botapp.fight.event.player.HealEvent;
import ru.tggc.botapp.formatter.fight.FightEventFormatter;
import ru.tggc.botapp.formatter.msgkey.FightMsgKey;
import ru.tggc.telegrambotcore.formatter.FormatService;

@Component
@RequiredArgsConstructor
public class HealEventFormatter implements FightEventFormatter<HealEvent> {
    private final FormatService formatService;

    @Override
    public Class<HealEvent> type() {
        return HealEvent.class;
    }

    @Override
    public String format(HealEvent event) {
        return formatService.random(FightMsgKey.PLAYER_HEAL, event.username(), event.heal());
    }
}
