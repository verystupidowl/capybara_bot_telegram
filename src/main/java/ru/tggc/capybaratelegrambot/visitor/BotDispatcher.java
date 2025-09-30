package ru.tggc.capybaratelegrambot.visitor;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.domain.dto.ChatDto;
import ru.tggc.capybaratelegrambot.domain.dto.UserDto;
import ru.tggc.capybaratelegrambot.domain.dto.response.Response;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.registry.CallbackHandleRegistry;
import ru.tggc.capybaratelegrambot.registry.MessageHandleRegistry;
import ru.tggc.capybaratelegrambot.registry.PhotoHandleRegistry;
import ru.tggc.capybaratelegrambot.service.UserService;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotDispatcher implements UpdateDispatcherVisitor {
    private final MessageHandleRegistry messageHandleRegistry;
    private final PhotoHandleRegistry photoHandleRegistry;
    private final CallbackHandleRegistry callbackHandleRegistry;
    private final UserService userService;
    private final InlineKeyboardCreator creator;

    @Async
    @Override
    public CompletableFuture<Response> onText(Message message) {
        return CompletableFuture.supplyAsync(() -> {
            saveOrUpdateUser(message.from(), message.chat());
            return messageHandleRegistry.dispatch(message);
        });
    }

    @Async
    @Override
    public CompletableFuture<Response> onCallback(CallbackQuery callbackQuery) {
        return CompletableFuture.supplyAsync(() -> {
            saveOrUpdateUser(callbackQuery.from(), callbackQuery.maybeInaccessibleMessage().chat());
            return callbackHandleRegistry.dispatch(callbackQuery);
        });
    }

    @Async
    @Override
    public CompletableFuture<Response> onPhoto(Message message) {
        return CompletableFuture.supplyAsync(() -> {
            saveOrUpdateUser(message.from(), message.chat());
            return photoHandleRegistry.dispatch(message);
        });
    }

    @Async
    @Override
    public CompletableFuture<Response> onGreetings(Message message) {
        return CompletableFuture.supplyAsync(() -> {
            long chatId = message.chat().id();
            log.info("New chat: {}", message.chat().title());
            SendMessage sm = new SendMessage(chatId, """
                    Привет! Я капибаработ!
                    Чтобы ты мог играть со мной, мне нужно дать права доступа
                    Как только ты это сделаешь, смело пиши "Взять капибару", чтобы начать играть\uD83D\uDCAB""");
            SendDocument sd = new SendDocument(
                    chatId,
                    "CgACAgQAAx0CZNf_xQACAYJk5heHk6jC9_LZtiqNjWZ5iwABkhcAAlsDAAJGV1VTOovbEOP3PPcwBA"
            );
            sd.replyMarkup(creator.takeCapybara());
            return Response.of(sm).andThen(Response.of(sd));
        });
    }

    private void saveOrUpdateUser(User from, Chat chat) {
        ChatDto chatDto = new ChatDto(chat.id(), chat.title());
        UserDto user = new UserDto(from.id().toString(), from.username());
        userService.saveOrUpdate(user, chatDto);
    }
}
