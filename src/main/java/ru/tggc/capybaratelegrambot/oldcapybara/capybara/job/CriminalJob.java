package ru.tggc.capybaratelegrambot.oldcapybara.capybara.job;


import com.pengrad.telegrambot.model.Message;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.Bot;
import ru.tggc.capybaratelegrambot.oldcapybara.capybara.Capybara;
import ru.tggc.capybaratelegrambot.oldcapybara.capybara.properties.CapybaraJob;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Deprecated(forRemoval = true)
public class CriminalJob implements Job {

    private Integer index;
    private CapybaraJob capybaraJob;
    private Integer rise;
    private final Random random = new Random();


    public CriminalJob(Integer index) {
        this.index = index;
    }

    public CriminalJob() {

    }

    @Override
    public Capybara goToWork(Message message, Capybara capybara) {
//        if (message.date() > capybara.getJob().getJobTimer().getNextJob()) {
//            if (capybara.getJob().getJobTimer().getLevel() == 0) {
//                if (capybara.getCapybaraBigJob().getLevel() != 1) {
//                    capybara.getJob().getJobTimer().setLevel(1);
//                    capybara.getJob().getJobTimer().setNextJob((long) (message.date() + 21600));
//                    capybara.getJob().getJobTimer().setTimer(message.date() + 7200);
//                } else
//                    throw new CapybaraException("3");
//            } else {
//                throw new CapybaraException("2");
//            }
//        } else {
//            throw new CapybaraException("1");
//        }
//        return capybara;
        return null;
    }

    @Override
    public Capybara getFromWork(Message message, Capybara capybara, Bot bot) {
//        if (message.date() > capybara.getJob().getJobTimer().getTimeRemaining()) {
//            if (capybara.getJob().getJobTimer().getLevel() == 1) {
//                Text text = new Text();
//                capybara.getJob().getJobTimer().setLevel(0);
//                int random1 = random.nextInt((capybara.getJob().getIndex() + 1) * 200 + 1);
//                capybara.setCurrency(random1 > 100 ? capybara.getCurrency() + random1 / 2 :
//                        capybara.getCurrency() - (capybara.getCurrency() / 10));
//                bot.execute(new SendMessage(message.chat().id(), random1 > 100 ? text.getCurrency(random1 / 2) : text.BUSTED));
//                if (capybara.getJob().getRise() + 1 >= 10 * (index + 1) && index <= 5) {
//                    capybara.getJob().setRise(1);
//                    capybara.getJob().setIndex(capybara.getJob().getIndex() + 1);
//                    capybara.setCurrency(capybara.getCurrency() + 150);
//                    bot.execute(new SendMessage(message.chat().id(), "Ух ты! Твоя капибара так усердно работала, что смогла получить повышение!" +
//                                    "\nПлюс 150 арбузных долек!!!"));
//                } else {
//                    if (Integer.parseInt(capybara.getJob().toString()) < 400)
//                        capybara.getJob().setRise(capybara.getJob().getRise() + 1);
//                }
//            } else {
//                throw new CapybaraException("2");
//            }
//        } else {
//            throw new CapybaraException("1");
//        }
//        return capybara;
        return null;
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
        jobs.add(new CriminalJob(0));
        jobs.add(new CriminalJob(1));
        jobs.add(new CriminalJob(2));
        return jobs;
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
        return "3" + index + "0";
    }

    @Override
    public String getStringJob(Capybara capybara) {
        return switch ((Integer.parseInt(capybara.getJob().toString()) / 10) % 10) {
            case 0 -> "Карманник";
            case 1 -> "Мелкий воришка";
            case 2 -> "Грабитель";
            case 3 -> "Гангстер";
            case 4 -> "Криминальный авторитет";
            case 5 -> "Босс мафии";
            default -> "";
        };
    }

    @Override
    public Jobs getEnum() {
        return Jobs.CRIMINAL;
    }

}
