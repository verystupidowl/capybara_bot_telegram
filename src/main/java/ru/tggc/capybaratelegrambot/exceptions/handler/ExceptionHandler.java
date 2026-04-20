package ru.tggc.capybaratelegrambot.exceptions.handler;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.domain.response.Response;
import ru.tggc.capybaratelegrambot.domain.response.ResponseBuilder;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraAlreadyExistsException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraHasNoMoneyException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraNotFoundException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraTiredException;
import ru.tggc.capybaratelegrambot.exceptions.UserNotFoundException;
import ru.tggc.capybaratelegrambot.keyboard.KeyboardFactory;
import ru.tggc.capybaratelegrambot.keyboard.KeyboardKey;
import ru.tggc.capybaratelegrambot.utils.Text;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.CompletionException;
import java.util.function.Function;

import static ru.tggc.capybaratelegrambot.utils.Utils.getOrElse;
import static ru.tggc.capybaratelegrambot.utils.Utils.ifPresent;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExceptionHandler {
    protected static final String DEFAULT_ERROR_MESSAGE = "Непредвиденная ошибка";

    private final KeyboardFactory keyboardFactory;

    public Response handleException(Exception e, Chat chat, User from) {
        Throwable cause = unwrap(e);
        Response response;
        long chatId = chat.id();
        switch (cause) {
            case CapybaraNotFoundException ex -> {
                log.info(ex.getMessage(), chatId);
                SendMessage message = new SendMessage(chatId, Text.DONT_HAVE_CAPYBARA);
                message.replyMarkup(keyboardFactory.getKeyboardInline(KeyboardKey.TAKE_CAPYBARA));
                response = Response.of(message);
            }
            case UserNotFoundException ex -> {
                log.info(ex.getMessage(), chatId);
                SendMessage message = new SendMessage(chatId, Text.DONT_HAVE_CAPYBARA);
                message.replyMarkup(keyboardFactory.getKeyboardInline(KeyboardKey.TAKE_CAPYBARA));
                response = Response.of(message);
            }
            case CapybaraAlreadyExistsException ex -> {
                log.info(ex.getMessage(), chatId);
                response = Response.of(new SendMessage(chatId, Text.ALREADY_HAVE_CAPYBARA));
            }
            case CapybaraHasNoMoneyException ex -> {
                log.info(ex.getMessage());
                String messageToSend = Text.NO_MONEY;
                response = Response.of(new SendMessage(chatId, messageToSend));
            }
            case CapybaraTiredException ex -> {
                SendMessage sm = new SendMessage(chatId, ex.getMessage());
                ifPresent(ex.getMarkup(), sm::replyMarkup);
                response = Response.of(sm);
            }
            case CapybaraException ex -> {
                log.info(ex.getMessage(), chatId);
                String messageToSend = ex.getMessageToSend();
                SendMessage sm = new SendMessage(chatId, Objects.requireNonNullElse(messageToSend, DEFAULT_ERROR_MESSAGE));
                ifPresent(ex.getMarkup(), sm::replyMarkup);
                response = Response.of(sm);
            }
            case NumberFormatException ignored -> response = Response.of(new SendMessage(chatId, "Введи число!"));
            default -> {
                log.error("Error invoking callback", cause);
                response = ResponseBuilder.toAdmin()
                        .message(buildMessageToAdmin(cause.getMessage(), chat, from))
                        .build()
                        .andThen(ResponseBuilder.to(chatId)
                                .message(DEFAULT_ERROR_MESSAGE)
                                .build());

            }
        }
        return response;
    }

    public String buildMessageToAdmin(String message, Chat chat, User from) {
        return LocalDateTime.now() + "\n" + from.username() + "\n" + getOrElse(chat.title(), Function.identity(), "Личка") + "\n" + message;
    }

    private Throwable unwrap(Throwable e) {
        if (e instanceof InvocationTargetException ite && ite.getCause() != null) {
            return unwrap(ite.getCause());
        }
        if (e instanceof CompletionException ce && ce.getCause() != null) {
            return unwrap(ce.getCause());
        }
        return e;
    }
}
