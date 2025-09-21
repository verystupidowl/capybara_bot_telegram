package ru.tggc.capybaratelegrambot.capybara.job;

import com.pengrad.telegrambot.model.Message;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.Bot;
import ru.tggc.capybaratelegrambot.capybara.Capybara;

import java.util.ArrayList;
import java.util.List;

@Deprecated(forRemoval = true)
@Component
public class MainJob {

    private Job job;

    public MainJob() {

    }

    public Job mainJob(Jobs jobs, Integer index) {

        switch (jobs) {
            case CASHIER -> {
                this.job = new CashierJob(index);
            }
            case CRIMINAL -> {
                this.job = new CriminalJob(index);
            }
            case PROGRAMMING -> {
                this.job = new ProgrammingJob(index);
            }
        }
        return job;
    }

    static {
        List<List<Job>> jobs = new ArrayList<>();

        List<Job> programmer = new ArrayList<>();
        programmer.add(new ProgrammingJob(0));
        programmer.add(new ProgrammingJob(1));
        programmer.add(new ProgrammingJob(2));

        List<Job> cashier = new ArrayList<>();
        cashier.add(new CashierJob(0));
        cashier.add(new CashierJob(1));
        cashier.add(new CashierJob(2));

        List<Job> criminal = new ArrayList<>();
        criminal.add(new CriminalJob(0));
        criminal.add(new CriminalJob(1));
        criminal.add(new CriminalJob(2));


        jobs.add(programmer);

        jobs.add(cashier);

        jobs.add(criminal);
    }

    public void work(Message message, Capybara capybara) {
        job.goToWork(message, capybara);
    }

    public void fromWork(Message message, Capybara capybara, Bot bot) {
        job.getFromWork(message, capybara, bot);
    }

}
