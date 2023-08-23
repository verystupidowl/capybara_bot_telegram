package ru.tggc.capibaraBotTelegram.capybara.job.bigJob;


import com.pengrad.telegrambot.model.Message;
import ru.tggc.capibaraBotTelegram.capybara.Capybara;

public interface Preparation {

    void goToPreparation(Message message, Capybara capybara);

    Capybara getFromPreparation(Message message, Capybara capybara);
}
