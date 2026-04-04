package ru.tggc.capybaratelegrambot.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import ru.tggc.capybaratelegrambot.service.TelegramBotService;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@EnableAsync
@EnableRetry
@Configuration
@Slf4j
@RequiredArgsConstructor
public class AsyncConfig implements AsyncConfigurer {
    @Value("${bot.admin-id}")
    private String adminId;
    private final TelegramBotService telegramBotService;

    @Bean
    public Executor taskExecutor() {
        return Executors.newFixedThreadPool(10);
    }

    @Override
    public @Nullable AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            telegramBotService.sendToAdmin(adminId);
            log.error(ex.getMessage(), ex);
        };
    }
}
