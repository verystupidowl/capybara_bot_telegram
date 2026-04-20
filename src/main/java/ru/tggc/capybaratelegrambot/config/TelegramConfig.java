package ru.tggc.capybaratelegrambot.config;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SetWebhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tggc.capybaratelegrambot.service.TelegramBotService;

@Configuration
@RequiredArgsConstructor
public class TelegramConfig {
    private final TelegramBotService telegramBotService;

    @Bean
    public TelegramBot telegramBot(@Value("${bot.token}") String botToken,
                                   @Value("${bot.webhook_url}") String webhookUrl) {
        TelegramBot bot = new TelegramBot(botToken);
        bot.execute(new SetWebhook().url(webhookUrl));
        telegramBotService.sendToAdmin("Деплой прошел успешно. Вебхук настроен");
        return bot;
    }
}
