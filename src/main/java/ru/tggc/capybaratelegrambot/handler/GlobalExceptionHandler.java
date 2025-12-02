package ru.tggc.capybaratelegrambot.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    public final TelegramBot telegramBot;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> handleException(Exception e) {
        telegramBot.execute(new SendMessage(428873987, e.getMessage()));
        log.error(e.getMessage(), e);
        return ResponseEntity.ok().build();
    }
}
