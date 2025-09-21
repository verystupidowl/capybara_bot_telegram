package ru.tggc.capybaratelegrambot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.aop.CallbackRegistry;
import ru.tggc.capybaratelegrambot.domain.dto.UserDto;
import ru.tggc.capybaratelegrambot.handler.text.TextHandler;
import ru.tggc.capybaratelegrambot.handler.callback.CallbackHandler;
import ru.tggc.capybaratelegrambot.service.UserService;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class Bot extends TelegramBot {
    private final TextHandler textCommandsHandler;
    private final CallbackHandler callbackHandler;
    private final UserService userService;

    public Bot(TextHandler textCommandsHandler, CallbackHandler callbackHandler, UserService userService, CallbackRegistry callbackRegistry) {
        super("6653668731:AAEP6-0Bd3H-Y8sUgo-9o9tAX8XkAq_mZ2Q");
        this.textCommandsHandler = textCommandsHandler;
        this.callbackHandler = callbackHandler;
        this.userService = userService;
    }

    public void run() {
        setUpdatesListener(updates -> {
            updates.stream()
                    .<Runnable>map(update -> () -> process(update))
                    .forEach(CompletableFuture::runAsync);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, exception -> log.error(exception.getMessage(), exception));
    }

    private void process(Update update) {
        if (update.message() != null) {
            User from = update.message().from();
            UserDto user = new UserDto(from.id().toString(), from.username());
            userService.saveOrUpdate(user);

            Optional.ofNullable(update.message().text())
                    .ifPresent(_ -> serveCommand(update.message()));
            Optional.ofNullable(update.message().photo())
                    .ifPresent(_ -> servePhoto(update.message()));
            if (!Arrays.stream(update.message().newChatMembers()).filter(member -> member.id() == 6653668731L).toList().isEmpty()) {
                greetings(update.message().chat().id());
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
        if (!Objects.equals(message.chat().id(), message.from().id())) {
            textCommandsHandler.serveTextCommandsPublic(message, this);
        } else {
            textCommandsHandler.handle(message, this);
        }
    }

    private void serveCallback(CallbackQuery callbackQuery) {
        callbackHandler.handle(callbackQuery);
    }

    private void servePhoto(Message message) {
        textCommandsHandler.servePhoto(message, this);
    }
}
