package ru.tggc.botapp.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tggc.telegrambotcore.formatter.FormatService;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TelegramConfig {

    @Bean
    public FormatService formatService(MessageSource messageSource) {
        return (key, args) -> messageSource.getMessage(key.getKey(), args, null);
    }
}
