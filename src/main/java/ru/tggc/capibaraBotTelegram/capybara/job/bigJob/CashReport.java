package ru.tggc.capibaraBotTelegram.capybara.job.bigJob;

import com.pengrad.telegrambot.model.Message;
import ru.tggc.capibaraBotTelegram.capybara.Capybara;
import ru.tggc.capibaraBotTelegram.capybara.properties.CapybaraBigJob;
import ru.tggc.capibaraBotTelegram.capybara.properties.CapybaraPreparation;

public class CashReport implements BigJob {

    private final Capybara capybara;

    public CashReport(Capybara capybara) {
        this.capybara = capybara;
    }

    @Override
    public Capybara goToBigJob(Message message) {
        capybara.setCapybaraBigJob(new CapybaraBigJob(message.date() +
                (capybara.getCapybaraPreparation().getImprove() == 3337 ?  7200 : 10800), 1, message.date() + 259200));
        return capybara;
    }

    @Override
    public Capybara getFromBigJob(Message message) {
        capybara.setCapybaraBigJob(new CapybaraBigJob(capybara.getCapybaraBigJob().getTimeRemaining(), 0, capybara.getCapybaraBigJob().getNextJob()));
        capybara.setCapybaraPreparation(new CapybaraPreparation(0, 0, 0));
        return capybara;
    }
}
