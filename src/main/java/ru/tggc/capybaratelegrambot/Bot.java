package ru.tggc.capybaratelegrambot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.visitor.UpdateDispatcherVisitor;
import ru.tggc.capybaratelegrambot.visitor.UpdateWrapper;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class Bot extends TelegramBot {
    private final UpdateDispatcherVisitor visitor;

    public Bot(@Value("${bot.token}") String botToken,
               UpdateDispatcherVisitor visitor) {
        super(botToken);
        this.visitor = visitor;
    }

    public void run() {
        setUpdatesListener(updates -> {
            updates.stream()
                    .<Runnable>map(update -> () -> serve(update))
                    .forEach(runnable -> CompletableFuture.runAsync(runnable)
                            .exceptionally(e -> {
                                e.fillInStackTrace();
                                return null;
                            }));
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, exception -> log.error(exception.getMessage(), exception));
    }

    private void serve(Update update) {
        UpdateWrapper wrapper = new UpdateWrapper(update);
        wrapper.accept(visitor)
                .thenCompose(response -> Optional.ofNullable(response)
                        .map(r -> r.send(this))
                        .orElseGet(() -> CompletableFuture.completedFuture(null)))
                .exceptionally(e -> {
                    log.error("Error while dispatching an update {} ", update, e);
                    return null;
                });
    }
}
