package ru.tggc.capybaratelegrambot.oldcapybara.capybara.job.bigJob;


import com.pengrad.telegrambot.model.Message;
import ru.tggc.capybaratelegrambot.oldcapybara.capybara.Capybara;

@Deprecated(forRemoval = true)
public interface BigJob {

    Capybara goToBigJob(Message message);

    Capybara getFromBigJob(Message message);
}
