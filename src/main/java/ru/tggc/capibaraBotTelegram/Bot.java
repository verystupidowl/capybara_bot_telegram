package ru.tggc.capibaraBotTelegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.tggc.capibaraBotTelegram.keyboard.SimpleKeyboardCreator;
import ru.tggc.capibaraBotTelegram.serveCommands.CallbackServer;
import ru.tggc.capibaraBotTelegram.serveCommands.TextCommandsServer;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class Bot extends TelegramBot {

    private final ExecutorService executorService = Executors.newFixedThreadPool(12);
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
            if (update.message().newChatMembers().length != 0) {
                if (!Arrays.stream(update.message().newChatMembers()).toList().stream().filter(member -> member.id() == 6653668731L).toList().isEmpty()) {
                    greetings(update.message().chat().id());
                }
            }
        } else if (update.callbackQuery() != null) {
            serveCallback(update.callbackQuery());
        }
    }

    private void greetings(Long chatId) {
        execute(new SendMessage(chatId, """
                Привет! Я капибаработ!
                Чтобы ты мог играть со мной, мне нужно дать права доступа
                Как только ты это сделаешь, смело пиши "Взять капибару", чтобы начать играть\uD83D\uDCAB"""));
        execute(new SendDocument(chatId, "CgACAgQAAx0CZNf_xQACAYJk5heHk6jC9_LZtiqNjWZ5iwABkhcAAlsDAAJGV1VTOovbEOP3PPcwBA"));
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
