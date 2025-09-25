package ru.tggc.capybaratelegrambot.oldcapybara.capybara.job.bigJob;


import com.pengrad.telegrambot.model.Message;
import ru.tggc.capybaratelegrambot.oldcapybara.capybara.Capybara;
import ru.tggc.capybaratelegrambot.oldcapybara.capybara.properties.CapybaraBigJob;
import ru.tggc.capybaratelegrambot.oldcapybara.capybara.properties.CapybaraPreparation;

@Deprecated(forRemoval = true)
public class BigItProject implements BigJob {

    private final Capybara capybara;

    public BigItProject(Capybara capybara) {
        this.capybara = capybara;
    }

    @Override
    public Capybara goToBigJob(Message message) {
        capybara.setCapybaraBigJob(new CapybaraBigJob(message.date() +
                (capybara.getCapybaraPreparation().getImprove() == 3337 ? 7200 : 10800), 1, (long) (message.date() + 259200)));
        return capybara;
    }

    @Override
    public Capybara getFromBigJob(Message message) {
        capybara.setCapybaraBigJob(new CapybaraBigJob(capybara.getCapybaraBigJob().getTimeRemaining(), 0, capybara.getCapybaraBigJob().getNextJob()));
        capybara.setCapybaraPreparation(new CapybaraPreparation(0, 0, 0L));
        return capybara;
    }
}
