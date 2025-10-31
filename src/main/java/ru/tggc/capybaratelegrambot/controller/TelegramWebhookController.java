package ru.tggc.capybaratelegrambot.controller;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.utility.BotUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tggc.capybaratelegrambot.service.BotService;

@RestController
@RequestMapping("/telegram")
@RequiredArgsConstructor
@Slf4j
public class TelegramWebhookController {
    private final BotService botService;

    @PostMapping("/webhook")
    public ResponseEntity<Void> onUpdateReceived(@RequestBody String updateJson) {
        Update update = BotUtils.parseUpdate(updateJson);
        botService.receiveUpdate(update);
        return ResponseEntity.ok().build();
    }
}
