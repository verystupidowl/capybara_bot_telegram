package ru.tggc.capybaratelegrambot.domain.response;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@FunctionalInterface
public interface Response {

    CompletableFuture<Void> send(TelegramBot bot);

    static <Rq extends BaseRequest<Rq, Rs>, Rs extends BaseResponse> Response ofAll(List<Rq> requests) {
        return bot -> CompletableFuture.runAsync(() -> requests.forEach(bot::execute));
    }

    @SafeVarargs
    static <Rq extends BaseRequest<Rq, Rs>, Rs extends BaseResponse> Response ofAll(Rq... requests) {
        return bot -> CompletableFuture.runAsync(() -> Arrays.stream(requests).forEach(bot::execute));
    }

    static <Rq extends BaseRequest<Rq, Rs>, Rs extends BaseResponse> Response of(BaseRequest<Rq, Rs> message) {
        return bot -> CompletableFuture.runAsync(() -> bot.execute(message));
    }

    static <T> Response of(BiConsumer<TelegramBot, T> consumer, T request) {
        return bot -> CompletableFuture.runAsync(() -> consumer.accept(bot, request));
    }

    static Response empty() {
        return bot -> CompletableFuture.completedFuture(null);
    }

    @NotNull
    default Response andThen(@NotNull Response after) {
        return bot -> this.send(bot).thenCompose(v -> after.send(bot));
    }
}