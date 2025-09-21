package ru.tggc.capybaratelegrambot.capybara.job.bigJob;


import com.pengrad.telegrambot.model.Message;
import ru.tggc.capybaratelegrambot.capybara.Capybara;
import ru.tggc.capybaratelegrambot.capybara.properties.CapybaraPreparation;

@Deprecated(forRemoval = true)
public class BigItProjectPreparation implements Preparation {

    private final int improvement;

    public BigItProjectPreparation(int improvement) {
        this.improvement = improvement;
    }

    @Override
    public void goToPreparation(Message message, Capybara capybara) {
    }

    @Override
    public Capybara getFromPreparation(Message message, Capybara capybara) {
        capybara.setCapybaraPreparation(new CapybaraPreparation(improvement,
                0, 1L));
        return capybara;
    }
}
