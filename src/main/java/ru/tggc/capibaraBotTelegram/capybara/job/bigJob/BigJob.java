package ru.tggc.capibaraBotTelegram.capybara.job.bigJob;


import com.pengrad.telegrambot.model.Message;
import ru.tggc.capibaraBotTelegram.capybara.Capybara;

public interface BigJob {

    Capybara goToBigJob(Message message);

    Capybara getFromBigJob(Message message);
}
