package ru.tggc.botapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"ru.tggc"})
public class BotAppApplication {

    static void main(String[] args) {
        SpringApplication.run(BotAppApplication.class, args);
    }

}
