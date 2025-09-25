package ru.tggc.capybaratelegrambot.oldcapybara.capybara.job.bigJob;


import com.pengrad.telegrambot.model.Message;
import ru.tggc.capybaratelegrambot.oldcapybara.capybara.Capybara;

@Deprecated(forRemoval = true)
public interface Preparation {

    void goToPreparation(Message message, Capybara capybara);

    Capybara getFromPreparation(Message message, Capybara capybara);
}
