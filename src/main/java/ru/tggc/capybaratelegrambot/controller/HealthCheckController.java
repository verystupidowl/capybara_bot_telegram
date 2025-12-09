package ru.tggc.capybaratelegrambot.controller;

import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tggc.capybaratelegrambot.domain.response.Response;
import ru.tggc.capybaratelegrambot.service.BotService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HealthCheckController {
    @Value("${bot.admin-id}")
    private String adminId;

    private final BotService botService;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        log.debug("health checked");
        botService.send(Response.of(new SendMessage(adminId, "health checked")));
        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }
}
