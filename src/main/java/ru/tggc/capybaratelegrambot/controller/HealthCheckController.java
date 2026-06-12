package ru.tggc.capybaratelegrambot.controller;

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

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        log.debug("health checked");
        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
