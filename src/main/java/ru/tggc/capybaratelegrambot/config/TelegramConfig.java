package ru.tggc.capybaratelegrambot.config;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetWebhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramConfig {

    @Bean
    public TelegramBot telegramBot(@Value("${bot.token}") String botToken,
                                   @Value("${bot.webhook_url}") String webhookUrl) {
        TelegramBot bot = new TelegramBot(botToken);
        bot.execute(new SetWebhook().url(webhookUrl));
        bot.execute(new SendMessage(428873987, "Деплой прошел успешно. Вебхук настроен"));
        return bot;
    }
}
