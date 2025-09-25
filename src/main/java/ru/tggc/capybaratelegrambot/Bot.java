package ru.tggc.capybaratelegrambot;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.aop.CallbackRegistry;
import ru.tggc.capybaratelegrambot.aop.MessageHandleRegistry;
import ru.tggc.capybaratelegrambot.domain.dto.UserDto;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.service.UserService;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class Bot extends TelegramBot {
    private final MessageHandleRegistry messageHandleRegistry;
    private final CallbackRegistry callbackRegistry;
    private final UserService userService;
    private final InlineKeyboardCreator creator;

    private static final int MAX_UPDATES = 10;

    Cache<Long, Integer> countOfUpdates = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(10))
            .maximumSize(1000)
            .build();

    public Bot(@Value("${bot.token}") String botToken,
               MessageHandleRegistry messageHandleRegistry,
               CallbackRegistry callbackRegistry,
               UserService userService, InlineKeyboardCreator creator) {
        super(botToken);
        this.messageHandleRegistry = messageHandleRegistry;
        this.callbackRegistry = callbackRegistry;
        this.userService = userService;
        this.creator = creator;
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
            if (checkUserAndUpdate(from, update.message().chat().id())) return;
            Optional.ofNullable(update.message().text())
                    .ifPresent(s -> serveCommand(update.message()));
//            Optional.ofNullable(update.message().photo())
//                    .ifPresent(_ -> servePhoto(update.message()));
            if (!Arrays.stream(update.message().newChatMembers()).filter(member -> member.id() == 6653668731L).toList().isEmpty()) {
                greetings(update.message().chat().id().toString());
            }
        } else if (update.callbackQuery() != null) {
            User from = update.callbackQuery().from();
            if (checkUserAndUpdate(from, update.callbackQuery().maybeInaccessibleMessage().chat().id())) return;
            serveCallback(update.callbackQuery());
            execute(new AnswerCallbackQuery(update.callbackQuery().id()));
        }
    }

    private boolean checkUserAndUpdate(User from, long chatId) {
        Integer count = countOfUpdates.getIfPresent(from.id());

        if (count != null && count > MAX_UPDATES) {
            log.info("user {} is trying to ddos", from.username());
            countOfUpdates.policy().expireAfterWrite().get().ageOf(from.id(), TimeUnit.SECONDS).ifPresentOrElse(ex -> {
                String time = MAX_UPDATES - ex + "c";
                String text = "Cлишком много запросов, попробуй снова через " + time;
                execute(new SendMessage(chatId, text));
            }, () -> {
                String text = "Cлишком много запросов";
                execute(new SendMessage(chatId, text));
            });
            return true;
        }
        int currentCount = count == null ? 0 : count;
        countOfUpdates.put(from.id(), currentCount + 1);
        UserDto user = new UserDto(from.id().toString(), from.username());
        userService.saveOrUpdate(user);
        return false;
    }

    private void greetings(String chatId) {
        execute(new SendMessage(chatId, """
                Привет! Я капибаработ!
                Чтобы ты мог играть со мной, мне нужно дать права доступа
                Как только ты это сделаешь, смело пиши "Взять капибару", чтобы начать играть\uD83D\uDCAB"""));
        SendDocument sendDocument = new SendDocument(
                chatId,
                "CgACAgQAAx0CZNf_xQACAYJk5heHk6jC9_LZtiqNjWZ5iwABkhcAAlsDAAJGV1VTOovbEOP3PPcwBA"
        );
        sendDocument.replyMarkup(creator.takeCapybara());
        execute(sendDocument);
    }

    private void serveCommand(Message message) {
        if (!Objects.equals(message.chat().id(), message.from().id())) {
            messageHandleRegistry.dispatch(message).accept(this);
        } else {
            messageHandleRegistry.dispatch(message).accept(this);
        }
    }

    private void serveCallback(CallbackQuery callbackQuery) {
        callbackRegistry.dispatch(callbackQuery).accept(this);
    }
//
//    private void servePhoto(Message message) {
//        messageHandleRegistry.servePhoto(message);
//    }
}
