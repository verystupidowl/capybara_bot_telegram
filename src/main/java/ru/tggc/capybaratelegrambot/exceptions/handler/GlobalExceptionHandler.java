package ru.tggc.capybaratelegrambot.exceptions.handler;

import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.tggc.capybaratelegrambot.domain.response.Response;
import ru.tggc.capybaratelegrambot.service.BotService;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    @Value("${bot.admin-id}")
    private String adminId;

    private final BotService botService;

    @ExceptionHandler(Exception.class)
    public void handleException(Exception e) {
        botService.send(Response.of(new SendMessage(adminId, e.getMessage())));
        log.error(e.getMessage(), e);
    }
}
