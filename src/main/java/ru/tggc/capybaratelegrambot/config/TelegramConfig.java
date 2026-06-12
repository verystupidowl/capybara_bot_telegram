package ru.tggc.capybaratelegrambot.config;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SetWebhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TelegramConfig {

    @Bean
    public TelegramBot telegramBot(@Value("${bot.token}") String botToken,
                                   @Value("${bot.webhook_url}") String webhookUrl) {
        TelegramBot bot = new TelegramBot(botToken);
        var response = bot.execute(new SetWebhook().url(webhookUrl));
        log.info("Webhook info {}", response);
        return bot;
    }
}
