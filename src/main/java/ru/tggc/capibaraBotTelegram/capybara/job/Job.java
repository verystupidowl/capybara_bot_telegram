package ru.tggc.capibaraBotTelegram.capybara.job;


import com.pengrad.telegrambot.model.Message;
import ru.tggc.capibaraBotTelegram.Bot;
import ru.tggc.capibaraBotTelegram.capybara.Capybara;
import ru.tggc.capibaraBotTelegram.capybara.properties.CapybaraJob;

import java.util.List;

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
