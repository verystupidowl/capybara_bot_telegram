package ru.tggc.capybaratelegrambot.visitor;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import ru.tggc.capybaratelegrambot.domain.response.Response;

import java.util.concurrent.CompletableFuture;

public interface UpdateDispatcherVisitor {

    CompletableFuture<Response> onText(Message message);

    CompletableFuture<Response> onCallback(CallbackQuery callbackQuery);

    CompletableFuture<Response> onPhoto(Message message);

    CompletableFuture<Response> onGreetings(Message message);
}
