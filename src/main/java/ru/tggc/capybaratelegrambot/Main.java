package ru.tggc.capybaratelegrambot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.tggc.capybaratelegrambot.config.SpringConfig;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

//    public static void main(String[] args) {
//        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
//        CapybaraDAO dao = context.getBean("capybaraDAO", CapybaraDAO.class);
//
//        System.out.println(dao.getCapybaraFromDB("428873987", "-1001963497529"));
//    }
}