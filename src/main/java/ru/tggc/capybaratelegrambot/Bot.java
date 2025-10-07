package ru.tggc.capybaratelegrambot;

import com.pengrad.telegrambot.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Bot extends TelegramBot {

    public Bot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }
}
