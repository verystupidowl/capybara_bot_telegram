package ru.tggc.capybaratelegrambot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tggc.capybaratelegrambot.service.TelegramBotService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HealthCheckController {
    private final TelegramBotService telegramBotService;
    @Value("${bot.admin-id}")
    private String adminId;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        log.debug("health checked");
        telegramBotService.sendToAdmin("health checked");
        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }
}
