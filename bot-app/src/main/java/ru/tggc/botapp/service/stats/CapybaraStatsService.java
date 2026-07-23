package ru.tggc.botapp.service.stats;

import com.pengrad.telegrambot.request.SendPhoto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.botapp.domain.dto.StatKey;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.Level;
import ru.tggc.botapp.domain.model.enums.Type;
import ru.tggc.botapp.formatter.msgkey.CommonMsgKey;
import ru.tggc.botapp.listener.event.NewLevelEvent;
import ru.tggc.botapp.listener.event.NewTypeEvent;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.formatter.FormatService;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CapybaraStatsService {
    @Value("${bot.photos.new-level}")
    private String newLevelPhoto;
    @Value("${bot.photos.new-type}")
    private String newTypePhoto;

    private final FormatService formatService;
    private final Map<StatKey<?>, CapybaraStats<?>> capybaraStats;
    private final ApplicationEventPublisher eventPublisher;

    public CapybaraStatsService(List<CapybaraStats<?>> capybaraStats,
                                FormatService formatService,
                                ApplicationEventPublisher eventPublisher) {
        this.capybaraStats = capybaraStats.stream()
                .collect(Collectors.toMap(CapybaraStats::getStatKey, Function.identity()));
        this.formatService = formatService;
        this.eventPublisher = eventPublisher;
    }

    @SuppressWarnings("unchecked")
    public <T> CapybaraStats<T> getStats(StatKey<T> statKey) {
        return (CapybaraStats<T>) capybaraStats.get(statKey);
    }

    @Transactional
    public <T> void modify(Capybara capybara, StatKey<T> statKey, Integer value) {
        CapybaraStats<T> stats = getStats(statKey);
        stats.modify(capybara, value);
        boolean isNewLevel = stats.checkNewLevel(capybara);

        if (isNewLevel) {
            newLevel(capybara);
            stats.setToDefault(statKey.extract(capybara));
        }
    }

    public void newLevel(Capybara capybara) {
        capybara.getLevel().setValue(capybara.getLevel().getValue() + 1);
        String message = formatService.get(
                CommonMsgKey.NEW_LEVEL,
                capybara.getUser().getUsername(),
                capybara.getName()
        );
        SendPhoto sp = new SendPhoto((long) capybara.getChat().getId(), newLevelPhoto);
        sp.caption(message);
        Response response = Response.of(sp);
        eventPublisher.publishEvent(new NewLevelEvent(response));

        checkNewType(capybara);
    }

    public void checkNewType(Capybara capybara) {
        Level level = capybara.getLevel();
        Type current = level.getType();
        if (level.getValue() >= current.getMaxLevel()) {
            Type next = current.next()
                    .orElseThrow();

            capybara.getLevel().setType(next);
            capybara.increaseMoney(next.getGift());

            String message = formatService.get(
                    CommonMsgKey.NEW_TYPE,
                    capybara.getUser().getUsername(),
                    capybara.getName(),
                    next.getLabel(),
                    next.getGift()
            );

            SendPhoto sp = new SendPhoto((long) capybara.getChat().getId(), newTypePhoto);
            sp.caption(message);
            Response response = Response.of(sp);
            eventPublisher.publishEvent(new NewTypeEvent(response));
        }
    }
}
