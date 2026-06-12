package ru.tggc.capybaratelegrambot.router;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.domain.dto.ChatDto;
import ru.tggc.capybaratelegrambot.domain.dto.UserDto;
import ru.tggc.capybaratelegrambot.domain.response.Response;
import ru.tggc.capybaratelegrambot.domain.response.ResponseBuilder;
import ru.tggc.capybaratelegrambot.registry.CallbackHandleRegistry;
import ru.tggc.capybaratelegrambot.registry.MessageHandleRegistry;
import ru.tggc.capybaratelegrambot.registry.PhotoHandleRegistry;
import ru.tggc.capybaratelegrambot.service.UserService;

import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramUpdateRouter {
    private final MessageHandleRegistry messageRegistry;
    private final CallbackHandleRegistry callbackRegistry;
    private final PhotoHandleRegistry photoRegistry;
    private final UserService userService;

    private static final long BOT_ID = 6653668731L;

    public Response route(Update update) {
        if (update.message() != null) {
            log.info("message {} from {}", update.message().text(), update.message().from().username());
            return handleMessage(update.message());
        } else if (update.callbackQuery() != null) {
            log.info("callback {} from {}", update.callbackQuery().data(), update.callbackQuery().from().username());
            return handleCallback(update.callbackQuery());
        }
        return Response.empty();
    }

    private Response handleMessage(Message m) {
        saveUser(m.from(), m.chat());

        if (isBotAdded(m)) {
            return handleGreetings(m);
        }

        if (m.photo() != null && m.photo().length > 0) {
            return photoRegistry.dispatch(m);
        }
        return messageRegistry.dispatch(m);
    }

    private Response handleCallback(CallbackQuery q) {
        saveUser(q.from(), q.maybeInaccessibleMessage().chat());

        Response logicResponse = callbackRegistry.dispatch(q);
        return logicResponse.andThen(Response.of(new AnswerCallbackQuery(q.id())));
    }

    private void saveUser(User from, Chat chat) {
        userService.saveOrUpdate(new UserDto(from.id(), from.username()), new ChatDto(chat.id(), chat.title()));
    }

    private boolean isBotAdded(Message m) {
        return m.newChatMembers() != null &&
                Arrays.stream(m.newChatMembers()).anyMatch(u -> u.id().equals(BOT_ID));
    }

    private Response handleGreetings(Message m) {
        return ResponseBuilder.to(m.chat().id())
                .message("Привет! Я капибара!")
                .build();
    }
}
