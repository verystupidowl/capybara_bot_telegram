package ru.tggc.capybaratelegrambot.capybara.job;


import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.Bot;
import ru.tggc.capybaratelegrambot.capybara.Capybara;
import ru.tggc.capybaratelegrambot.capybara.properties.CapybaraJob;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;
import ru.tggc.capybaratelegrambot.utils.Text;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Deprecated(forRemoval = true)
public class ProgrammingJob implements Job {

    private Integer index;
    private Integer rise;
    private CapybaraJob capybaraJob;
    private final Random random = new Random();

    public ProgrammingJob(Integer index) {
        this.index = index;
    }

    public ProgrammingJob() {

    }

    @Override
    public Capybara goToWork(Message message, Capybara capybara) {
        if (message.date() > capybara.getJob().getJobTimer().getNextJob()) {
            if (capybara.getJob().getJobTimer().getLevel() == 0) {
                if (capybara.getCapybaraBigJob().getLevel() != 1) {
                    capybara.getJob().getJobTimer().setLevel(1);
                    capybara.getJob().getJobTimer().setNextJob((long) (message.date() + 21600));
                    capybara.getJob().getJobTimer().setTimer(message.date() + 7200);
                } else
                    throw new CapybaraException("3");
            } else {
                throw new CapybaraException("2");
            }
        } else {
            throw new CapybaraException("1");
        }
        return capybara;
    }

    @Override
    public Capybara getFromWork(Message message, Capybara capybara, Bot bot) {
        if (message.date() > capybara.getJob().getJobTimer().getTimeRemaining()) {
            if (capybara.getJob().getJobTimer().getLevel() == 1) {
                Text text = new Text();
                capybara.getJob().getJobTimer().setLevel(0);
                int random1 = capybara.getJob().getIndex() != 0 ? random.nextInt(capybara.getJob().getIndex() * 100) + 100 : 0;
                capybara.setCurrency(capybara.getCurrency() + random1);
                bot.execute(new SendMessage(message.chat().id(), text.getCurrency(random1)));
                if (capybara.getJob().getRise() + 1 >= 10 * (index + 1) && index <= 5) {
                    capybara.getJob().setRise(1);
                    capybara.getJob().setIndex(capybara.getJob().getIndex() + 1);
                    capybara.setCurrency(capybara.getCurrency() + 150);
                    bot.execute(new SendMessage(message.chat().id(), "Ух ты! Твоя капибара так усердно работала, что смогла получить повышение!" +
                                    "\nПлюс 150 арбузных долек!!!"));
                } else {
                    if (Integer.parseInt(capybara.getJob().toString()) < 200)
                        capybara.getJob().setRise(capybara.getJob().getRise() + 1);
                }
            } else {
                throw new CapybaraException("2");
            }
        } else {
            throw new CapybaraException("1");
        }
        return capybara;
    }


    @Override
    public Integer getIndex() {
        return index;
    }

    @Override
    public void setIndex(Integer index) {
        this.index = index;
    }

    @Override
    public List<Job> getList() {
        List<Job> jobs = new ArrayList<>();
        jobs.add(new ProgrammingJob(0));
        jobs.add(new ProgrammingJob(1));
        jobs.add(new ProgrammingJob(2));
        return jobs;
    }

    @Override
    public String getStringJob(Capybara capybara) {
        return switch ((Integer.parseInt(capybara.getJob().toString()) / 10) % 10) {
            case 0 -> "Обучающийся на программиста";
            case 1 -> "Джуниор разработчик";
            case 2 -> "Миддл разработчик";
            case 3 -> "Сеньор разработчик";
            case 4 -> "Тим лид";
            case 5 -> "Директор IT компании";
            default -> "";
        };
    }

    @Override
    public Jobs getEnum() {
        return Jobs.PROGRAMMING;
    }

    @Override
    public CapybaraJob getJobTimer() {
        return capybaraJob;
    }

    @Override
    public void setJobTimer(CapybaraJob capybaraJob) {
        this.capybaraJob = capybaraJob;
    }

    @Override
    public Integer getRise() {
        return rise;
    }

    @Override
    public void setRise(Integer rise) {
        this.rise = rise;
    }

    @Override
    public String toString() {
        return "1" + index + "0";
    }

}
