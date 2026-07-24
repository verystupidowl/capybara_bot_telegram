package ru.tggc.botapp.service.stats;

import com.pengrad.telegrambot.request.SendPhoto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.Level;
import ru.tggc.botapp.domain.model.enums.Type;
import ru.tggc.botapp.formatter.msgkey.CommonMsgKey;
import ru.tggc.botapp.listener.event.NewLevelEvent;
import ru.tggc.botapp.listener.event.NewTypeEvent;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.formatter.FormatService;

@RequiredArgsConstructor
public abstract class AbstractPropertyService<T> implements CapybaraStats<T> {
    @Value("${bot.photos.new-level}")
    private String newLevelPhoto;
    @Value("${bot.photos.new-type}")
    private String newTypePhoto;

    private final FormatService formatService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void modify(Capybara capybara, Integer value) {
        modifyProperty(capybara, value);
        boolean isNewLevel = checkNewLevel(capybara);

        if (isNewLevel) {
            newLevel(capybara);
            setToDefault(getStatKey().extract(capybara));
        }
    }

    protected abstract void modifyProperty(Capybara capybara, Integer value);

    protected abstract boolean checkNewLevel(Capybara capybara);

    protected abstract void setToDefault(T stat);

    private void newLevel(Capybara capybara) {
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

    private void checkNewType(Capybara capybara) {
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
