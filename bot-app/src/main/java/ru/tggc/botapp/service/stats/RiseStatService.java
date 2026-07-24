package ru.tggc.botapp.service.stats;

import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.botapp.domain.dto.StatKey;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.Work;
import ru.tggc.botapp.formatter.msgkey.WorkMsgKey;
import ru.tggc.botapp.listener.event.NewRiseEvent;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.formatter.FormatService;

@Service
@RequiredArgsConstructor
public class RiseStatService implements CapybaraStats<Integer> {
    private final ApplicationEventPublisher eventPublisher;
    private final FormatService formatService;

    @Override
    @Transactional
    public void modify(Capybara capybara, Integer value) {
        Work work = capybara.getWork();
        work.setRise(work.getRise() + 1);
        checkRise(capybara);
    }

    private void checkRise(Capybara capybara) {
        Work work = capybara.getWork();
        if (work.getRise() >= 10 * (work.getIndex() + 1) && work.getIndex() <= 5) {
            work.setRise(1);
            work.setIndex(work.getIndex() + 1);
            capybara.increaseMoney(150);

            SendMessage sendMessage = new SendMessage(
                    (long) capybara.getChat().getId(),
                    formatService.get(WorkMsgKey.NEW_RISE)
            );
            Response response = Response.of(sendMessage);
            eventPublisher.publishEvent(new NewRiseEvent(response));
        }
    }

    @Override
    public StatKey<Integer> getStatKey() {
        return StatKey.RISE;
    }
}
