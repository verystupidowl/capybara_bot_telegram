package ru.tggc.capybaratelegrambot.oldcapybara.capybara.job.bigJob;


import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import ru.tggc.capybaratelegrambot.Bot;
import ru.tggc.capybaratelegrambot.oldcapybara.capybara.Capybara;
import ru.tggc.capybaratelegrambot.oldcapybara.capybara.properties.CapybaraPreparation;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.utils.Text;

import java.util.Random;

@Deprecated(forRemoval = true)
public class BigJobMain {

    private final Bot bot;

    private final InlineKeyboardCreator keyboardCreator = new InlineKeyboardCreator();

    public BigJobMain(Bot bot) {
        this.bot = bot;
    }

    public Capybara goWork(Capybara capybara, Message message) {
        if (capybara.hasWork() && capybara.getCapybaraBigJob().getNextJob() <= message.date() && capybara.getCapybaraBigJob().getLevel() == 0
                && capybara.getCapybaraPreparation().getPrepared() == 1) {
            switch (capybara.getJob().getEnum()) {
                case CRIMINAL -> {
                    bot.execute(
                            new SendMessage(message.chat().id(), "Твоя капибара пошла на ограбление!\nПожелай ей удачи!")
                    );
                    Robbery robbery = new Robbery(capybara);
                    capybara = robbery.goToBigJob(message);
                }
                case CASHIER -> {
                    bot.execute(
                            new SendMessage(message.chat().id(), "Твоя капибара начала работу над большим отчетом!\nПожелай ей удачи!")
                    );
                    CashReport cashReport = new CashReport(capybara);
                    capybara = cashReport.goToBigJob(message);
                }
                case PROGRAMMING -> {
                    bot.execute(
                            new SendMessage(message.chat().id(), "Твоя капибара начала работу над большим айти проектом!\nПожелай ей удачи!"))
                    ;
                    BigItProject project = new BigItProject(capybara);
                    capybara = project.goToBigJob(message);
                }
            }
        }
        return capybara;
    }

    public Capybara startPreparation(Capybara capybara, Message message) {
        if (capybara.hasWork() && capybara.getCapybaraBigJob().getNextJob() <= message.date() &&
                capybara.getCapybaraPreparation().getPrepared() == 0) {
            if (capybara.getJob().getJobTimer().getLevel() != 1) {
                switch (capybara.getJob().getEnum()) {
                    case CRIMINAL -> {
                        RobberyPreparation preparation = new RobberyPreparation(0);
                        bot.execute(new SendMessage(message.chat().id(), """
                                Твоя капибара пошла на подготовку ограблению!
                                Чтобы начать ограбление, тебе нужно купить претметы!
                                Вот список
                                Вперед!"""));
                        bot.execute(new SendMessage(message.chat().id(), Text.LIST_OF_THINGS_FOR_ROBBERY)
                                .replyMarkup(keyboardCreator.robberyImprovement()));
                        preparation.goToPreparation(message, capybara);
                    }
                    case CASHIER -> {
                        CashReportPreparation preparation2 = new CashReportPreparation(0);
                        bot.execute(new SendMessage(message.chat().id(), """
                                Твоя капибара пошла на подготовку к большому айти проекту!
                                Чтобы начать его, тебе нужно купить претметы!
                                Вот список
                                Вперед!"""));
                        bot.execute(new SendMessage(message.chat().id(), Text.LIST_OF_THINGS_FOR_CASH_REPORT)
                                .replyMarkup(keyboardCreator.cashReportImprovement()));
                        preparation2.goToPreparation(message, capybara);
                    }
                    case PROGRAMMING -> {
                        BigItProjectPreparation preparation1 = new BigItProjectPreparation(0);
                        bot.execute(new SendMessage(message.chat().id(), """
                                Твоя капибара пошла на подготовку к большому айти проекту!
                                Чтобы начать его, тебе нужно купить претметы!
                                Вот список
                                Вперед!"""));
                        bot.execute(new SendMessage(message.chat().id(), Text.LIST_OF_THINGS_FOR_BIG_IT_PROJECT)
                                .replyMarkup(keyboardCreator.bigItProject()));
                        preparation1.goToPreparation(message, capybara);
                    }
                }
            } else
                bot.execute(new SendMessage(message.chat().id(), "Сначала забери свою капибару с работы!"));
        }
        return capybara;
    }

    public Capybara finishPreparation(Capybara capybara, Message message, int improvement) {
        if (capybara.getCapybaraPreparation().getPrepared() == 0
                && capybara.getCapybaraPreparation().getImprovement() <= message.date()) {
            switch (capybara.getJob().getEnum()) {
                case CRIMINAL -> {
                    bot.execute(new SendMessage(message.chat().id(), """
                            Вот это да! Теперь твоя капибара готова к ограблению!
                            Напиши "Большое дело", чтобы начать.
                            Стоимость - 250 арбузных долек""")
                            .replyMarkup(keyboardCreator.bigJobKeyboard()));
                    RobberyPreparation preparation = new RobberyPreparation(improvement);
                    capybara = preparation.getFromPreparation(message, capybara);
                }
                case CASHIER -> {
                    bot.execute(new SendMessage(message.chat().id(), """
                            Вот это да! Теперь твоя капибара готова к большому отчету проекту!
                            Напиши "Большое дело", чтобы начать.
                            Стоимость - 250 арбузных долек""")
                            .replyMarkup(keyboardCreator.bigJobKeyboard()));
                    CashReportPreparation preparation2 = new CashReportPreparation(improvement);
                    capybara = preparation2.getFromPreparation(message, capybara);
                }
                case PROGRAMMING -> {
                    bot.execute(new SendMessage(message.chat().id(), """
                            Вот это да! Теперь твоя капибара готова к большому айти проекту!
                            Напиши "Большое дело", чтобы начать.
                            Стоимость - 250 арбузных долек""")
                            .replyMarkup(keyboardCreator.bigJobKeyboard()));
                    BigItProjectPreparation preparation1 = new BigItProjectPreparation(improvement);
                    capybara = preparation1.getFromPreparation(message, capybara);
                }
            }
        } else {
            capybara.setCapybaraPreparation(new CapybaraPreparation(0, 0, 0L));
        }
        return capybara;
    }

    public Capybara getFromBigJob(Capybara capybara, Message message) {
        if (capybara.hasWork() && capybara.getCapybaraBigJob().getLevel() == 1 && capybara.getCapybaraBigJob().getTimeRemaining() <= message.date()) {
            switch (capybara.getJob().getEnum()) {
                case CRIMINAL -> {
                    int rand = new Random().nextInt(500) + 300 + capybara.getLevel();
                    int currency = capybara.getCapybaraPreparation().getImprove() == 1337 ? (rand >= 450 ? rand : 50) : (rand >= 550 ? rand : 50);
                    capybara.setCurrency(capybara.getCurrency() + currency + (capybara.getCapybaraPreparation().getImprove() == 300 ?
                            capybara.getCapybaraPreparation().getImprove() : 0));
                    Robbery robbery = new Robbery(capybara);
                    capybara = robbery.getFromBigJob(message);
                    if (currency != 50) {
                        bot.execute(
                                new SendMessage(message.chat().id(), "Твоя капибара вернулась с ограбления и заработала целых " + rand + " арбузных долек!!!")
                        );
                    } else {
                        bot.execute(
                                new SendMessage(message.chat().id(), "КАПЕЦ! ТВОЮ КАПИБАРУ ПОЙМАЛИ И ОТНЯЛИ ВСЕ АРБУЗНЫЕ ДОЛЬКИ!!!")
                        );
                    }
                }
                case CASHIER -> {
                    int rand2 = new Random().nextInt(300) + 200 + capybara.getLevel();
                    int currency2 = capybara.getCapybaraPreparation().getImprove() == 1337 ? (rand2 >= 50 ? rand2 :
                            capybara.getCurrency()) : (rand2 >= 100 ? rand2 : capybara.getCurrency());
                    if (currency2 != capybara.getCurrency()) {
                        bot.execute(
                                new SendMessage(message.chat().id(), "Твоя капибара доделала отчет и заработала целых " + rand2 + " арбузных долек!!!")
                        );
                    } else {
                        bot.execute(
                                new SendMessage(message.chat().id(), "КАПЕЦ! ТВОЯ КАПИБАРА НЕ УСПЕЛА ДОДЕЛАТЬ ОТЧЕТ И НЕ ПОЛУЧИЛА АРБУЗНЫХ ДОЛЕК!!!")
                        );
                    }
                    capybara.setCurrency(capybara.getCurrency() + currency2 + (capybara.getCapybaraPreparation().getImprove() == 300 ?
                            capybara.getCapybaraPreparation().getImprove() : 0));
                    CashReport report = new CashReport(capybara);
                    capybara = report.getFromBigJob(message);
                }
                case PROGRAMMING -> {
                    int rand1 = new Random().nextInt(500) + 200 + capybara.getLevel();
                    int currency1 = capybara.getCapybaraPreparation().getImprove() == 1337 ? (rand1 >= 250 ? rand1 :
                            capybara.getCurrency()) : (rand1 >= 350 ? rand1 : capybara.getCurrency());
                    if (currency1 != capybara.getCurrency()) {
                        bot.execute(
                                new SendMessage(message.chat().id(), "Твоя капибара доделала проект и заработала целых " + rand1 + " арбузных долек!!!")
                        );
                    } else {
                        bot.execute(
                                new SendMessage(message.chat().id(), "КАПЕЦ! ТВОЯ КАПИБАРА НЕ УСПЕЛА ДОДЕЛАТЬ ПРОЕКТ И НЕ ПОЛУЧИЛА АРБУЗНЫХ ДОЛЕК!!!")
                        );
                    }
                    capybara.setCurrency(capybara.getCurrency() + currency1 + (capybara.getCapybaraPreparation().getImprove() == 300 ?
                            capybara.getCapybaraPreparation().getImprove() : 0));
                    BigItProject project = new BigItProject(capybara);
                    capybara = project.getFromBigJob(message);
                }
            }
        }
        return capybara;
    }
}
