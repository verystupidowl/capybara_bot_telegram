package ru.tggc.botapp.formatter.fight.boss;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tggc.botapp.fight.event.boss.TailMudSplashEvent;
import ru.tggc.botapp.formatter.fight.FightEventFormatter;
import ru.tggc.botapp.formatter.msgkey.FightMsgKey;
import ru.tggc.telegrambotcore.formatter.FormatService;

@Service
@RequiredArgsConstructor
public class TailMudSplashEventFormatter implements FightEventFormatter<TailMudSplashEvent> {
    private final FormatService formatService;

    @Override
    public Class<TailMudSplashEvent> type() {
        return TailMudSplashEvent.class;
    }

    @Override
    public String format(TailMudSplashEvent event) {
        return formatService.get(FightMsgKey.BOSS_TAIL_MUD_SPLASH);
    }
}
