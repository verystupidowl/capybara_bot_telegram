package ru.tggc.capybaratelegrambot.visitor;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import ru.tggc.capybaratelegrambot.domain.dto.response.Response;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public record UpdateWrapper(Update update) {

    public CompletableFuture<Response> accept(UpdateDispatcherVisitor visitor) {
        return Optional.ofNullable(update.message())
                .map(m -> {
                    if (m.newChatMembers() != null &&
                            Arrays.stream(m.newChatMembers()).anyMatch(u -> u.id() == 6653668731L)) {
                        return visitor.onGreetings(m);
                    } else if (m.photo() != null && m.photo().length > 0) {
                        return visitor.onPhoto(m);
                    } else if (m.text() != null) {
                        return visitor.onText(m);
                    }
                    return CompletableFuture.completedFuture(Response.empty());
                })
                .orElseGet(() -> {
                    CallbackQuery callbackQuery = update.callbackQuery();
                    if (callbackQuery != null) {
                        return visitor.onCallback(callbackQuery)
                                .thenApply(r -> r.andThen(Response.of(new AnswerCallbackQuery(callbackQuery.id()))));
                    }
                    return CompletableFuture.completedFuture(Response.empty());
                });
    }
}
