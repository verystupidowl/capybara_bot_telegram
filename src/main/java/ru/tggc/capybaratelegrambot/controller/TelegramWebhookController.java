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
import ru.tggc.capybaratelegrambot.Bot;
import ru.tggc.capybaratelegrambot.visitor.UpdateDispatcherVisitor;
import ru.tggc.capybaratelegrambot.visitor.UpdateWrapper;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/telegram")
@RequiredArgsConstructor
@Slf4j
public class TelegramWebhookController {
    private final UpdateDispatcherVisitor visitor;
    private final Bot bot;

    @PostMapping("/webhook")
    public ResponseEntity<Void> onUpdateReceived(@RequestBody String updateJson) {
        Update update = BotUtils.parseUpdate(updateJson);
        UpdateWrapper wrapper = new UpdateWrapper(update);

        wrapper.accept(visitor)
                .thenCompose(response -> Optional.ofNullable(response)
                        .map(r -> r.send(bot))
                        .orElseGet(() -> CompletableFuture.completedFuture(null)))
                .exceptionally(e -> {
                    log.error("Error while dispatching an update {}", update, e);
                    return null;
                });

        return ResponseEntity.ok().build();
    }
}
