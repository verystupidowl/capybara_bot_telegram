package ru.tggc.capybaratelegrambot.capybara.job;


import com.pengrad.telegrambot.model.Message;
import ru.tggc.capybaratelegrambot.Bot;
import ru.tggc.capybaratelegrambot.capybara.Capybara;
import ru.tggc.capybaratelegrambot.capybara.properties.CapybaraJob;

import java.util.List;

@Deprecated(forRemoval = true)
public interface Job {

    Capybara goToWork(Message message, Capybara capybara);

    Capybara getFromWork(Message message, Capybara capybara, Bot bot);

    Integer getIndex();

    void setIndex(Integer index);

    List<Job> getList();

    CapybaraJob getJobTimer();

    void setJobTimer(CapybaraJob capybaraJob);

    Integer getRise();

    void setRise(Integer rise);

    String getStringJob(Capybara capybara);

    Jobs getEnum();

}
