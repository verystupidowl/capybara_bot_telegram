package ru.tggc.capibaraBotTelegram;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.tggc.capibaraBotTelegram.DataBase.CapybaraDAO;
import ru.tggc.capibaraBotTelegram.capybara.Capybara;
import ru.tggc.capibaraBotTelegram.config.SpringConfig;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        Bot bot = context.getBean("bot", Bot.class);
        bot.run();
    }

//    public static void main(String[] args) {
//        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
//        CapybaraDAO dao = context.getBean("capybaraDAO", CapybaraDAO.class);
//
//        System.out.println(dao.getCapybaraFromDB("428873987", "-1001963497529"));
//    }
}