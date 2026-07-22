package ru.tggc.botapp.exceptions.handler;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.tggc.botapp.exceptions.CapybaraAlreadyExistsException;
import ru.tggc.botapp.exceptions.CapybaraException;
import ru.tggc.botapp.exceptions.CapybaraHasNoMoneyException;
import ru.tggc.botapp.exceptions.CapybaraNotFoundException;
import ru.tggc.botapp.exceptions.CapybaraTiredException;
import ru.tggc.botapp.exceptions.UserNotFoundException;
import ru.tggc.botapp.formatter.msgkey.ErrorMsgKey;
import ru.tggc.botapp.keyboard.KeyboardType;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.ResponseBuilder;
import ru.tggc.telegrambotcore.exception.ExceptionHandler;
import ru.tggc.telegrambotcore.formatter.FormatService;
import ru.tggc.telegrambotcore.keyboard.KeyboardFactory;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.CompletionException;
import java.util.function.Function;

import static ru.tggc.telegrambotcore.util.Utils.getOrElse;
import static ru.tggc.telegrambotcore.util.Utils.ifPresent;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExceptionHandlerImpl implements ExceptionHandler {
    protected static final String DEFAULT_ERROR_MESSAGE = "Непредвиденная ошибка";

    @Value("${telegram.admin-id}")
    private Long adminId;

    private final KeyboardFactory keyboardFactory;
    private final FormatService formatService;

    @NotNull
    public Response handleException(@NotNull Exception e, Chat chat, @NotNull User from) {
        Throwable cause = unwrap(e);
        Response response;
        long chatId = chat.id();
        switch (cause) {
            case CapybaraNotFoundException ex -> {
                log.info(ex.getMessage(), chatId);
                SendMessage message = new SendMessage(chatId, formatService.get(ErrorMsgKey.CAPYBARA_NOT_FOUND));
                message.replyMarkup(keyboardFactory.getKeyboardInline(KeyboardType.TAKE_CAPYBARA));
                response = Response.of(message);
            }
            case UserNotFoundException ex -> {
                log.info(ex.getMessage(), chatId);
                SendMessage message = new SendMessage(chatId, formatService.get(ErrorMsgKey.CAPYBARA_NOT_FOUND));
                message.replyMarkup(keyboardFactory.getKeyboardInline(KeyboardType.TAKE_CAPYBARA));
                response = Response.of(message);
            }
            case CapybaraAlreadyExistsException ex -> {
                log.info(ex.getMessage(), chatId);
                response = Response.of(new SendMessage(chatId, formatService.get(ErrorMsgKey.ALREADY_HAVE)));
            }
            case CapybaraHasNoMoneyException ex -> {
                log.info(ex.getMessage());
                String messageToSend = formatService.get(ErrorMsgKey.NO_MONEY);
                response = Response.of(new SendMessage(chatId, messageToSend));
            }
            case CapybaraTiredException ex -> {
                SendMessage sm = new SendMessage(chatId, ex.getMessage());
                ifPresent(ex.getMarkup(), sm::replyMarkup);
                response = Response.of(sm);
            }
            case CapybaraException ex -> {
                String messageToSend;
                if (ex.hasMsgKey()) {
                    messageToSend = formatService.get(ex.getMsgKey());
                } else {
                    messageToSend = ex.getMessageToSend();
                }

                log.warn(ex.getMessage(), ex);
                SendMessage sm = new SendMessage(chatId, Objects.requireNonNullElse(messageToSend, DEFAULT_ERROR_MESSAGE));
                ifPresent(ex.getMarkup(), sm::replyMarkup);
                response = Response.of(sm);
            }
            case NumberFormatException ignored -> response = Response.of(new SendMessage(chatId, "Введи число!"));
            default -> {
                log.error("Error invoking callback", cause);
                response = ResponseBuilder.to(adminId)
                        .message(buildMessageToAdmin(cause.getMessage(), chat, from))
                        .build()
                        .andThen(ResponseBuilder.to(chatId)
                                .message(DEFAULT_ERROR_MESSAGE)
                                .build());

            }
        }
        return response;
    }

    @NotNull
    public String buildMessageToAdmin(@NotNull String message, Chat chat, User from) {
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
