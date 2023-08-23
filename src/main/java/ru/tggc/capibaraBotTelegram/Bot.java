package ru.tggc.capibaraBotTelegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.tggc.capibaraBotTelegram.keyboard.SimpleKeyboardCreator;
import ru.tggc.capibaraBotTelegram.serveCommands.CallbackServer;
import ru.tggc.capibaraBotTelegram.serveCommands.TextCommandsServer;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class Bot extends TelegramBot {

    private final ExecutorService executorService = Executors.newFixedThreadPool(8);
    private final SimpleKeyboardCreator keyboardCreator = new SimpleKeyboardCreator();


    private final TextCommandsServer textCommandsServer;
    private final CallbackServer callbackServer;

    @Autowired
    public Bot(TextCommandsServer textCommandsServer, CallbackServer callbackServer) {
        super("6653668731:AAEP6-0Bd3H-Y8sUgo-9o9tAX8XkAq_mZ2Q");
        this.textCommandsServer = textCommandsServer;
        this.callbackServer = callbackServer;
    }

    public void run() {
        this.setUpdatesListener(updates -> {
            updates.stream()
                    .<Runnable>map(update -> () -> process(update))
                    .forEach(executorService::submit);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, exception -> System.out.println(exception.response().description()));
    }

    private void process(Update update) {
        if (update.message() != null) {
            Optional.ofNullable(update.message().text())
                    .ifPresent(command -> serveCommand(update.message()));
            Optional.ofNullable(update.message().photo())
                    .ifPresent(photo -> servePhoto(update.message()));
        } else if (update.callbackQuery() != null) {
            serveCallback(update.callbackQuery());
        }
    }

    private void serveCommand(Message message) {
        textCommandsServer.serveTextCommands(message, this);
    }

    private void serveCallback(CallbackQuery callbackQuery) {
        callbackServer.serveCallbackCommands(callbackQuery, this);
    }

    private void servePhoto(Message message) {
        textCommandsServer.servePhoto(message, this);
    }
}
