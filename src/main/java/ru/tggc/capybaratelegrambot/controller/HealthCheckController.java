package ru.tggc.capybaratelegrambot.controller;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HealthCheckController {
    private final TelegramBot telegramBot;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        log.info("health checked");
        telegramBot.execute(new SendMessage(428873987, "health checked"));
        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }
}
