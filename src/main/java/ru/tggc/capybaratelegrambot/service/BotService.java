package ru.tggc.capybaratelegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.visitor.UpdateDispatcherVisitor;
import ru.tggc.capybaratelegrambot.visitor.UpdateWrapper;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class BotService {
    private final TelegramBot bot;
    private final UpdateDispatcherVisitor visitor;

    public void receiveUpdate(Update update) {
        UpdateWrapper wrapper = new UpdateWrapper(update);

        wrapper.accept(visitor)
                .thenCompose(response -> Optional.ofNullable(response)
                        .map(r -> r.send(bot))
                        .orElseGet(() -> CompletableFuture.completedFuture(null)))
                .exceptionally(e -> {
                    log.error("Error while dispatching an update {}", update, e);
                    return null;
                });
    }
}
