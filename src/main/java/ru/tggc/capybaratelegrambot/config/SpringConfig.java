package ru.tggc.capybaratelegrambot.config;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tggc.capybaratelegrambot.Bot;

@Configuration
public class SpringConfig {

    @Bean
    public TelegramBot telegramBot(Bot bot) {
        bot.run();
        return bot;
    }
}
