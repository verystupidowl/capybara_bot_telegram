package ru.tggc.capibaraBotTelegram.capybara.job.bigJob;


import com.pengrad.telegrambot.model.Message;
import ru.tggc.capibaraBotTelegram.capybara.Capybara;
import ru.tggc.capibaraBotTelegram.capybara.properties.CapybaraPreparation;

public class RobberyPreparation implements Preparation {

    private final int improvement;

    public RobberyPreparation(int improvement) {
        this.improvement = improvement;
    }


    @Override
    public void goToPreparation(Message message, Capybara capybara) {
    }

    @Override
    public Capybara getFromPreparation(Message message, Capybara capybara) {
        capybara.setCapybaraPreparation(new CapybaraPreparation(improvement,
                0, 1));
        return capybara;
    }
}
