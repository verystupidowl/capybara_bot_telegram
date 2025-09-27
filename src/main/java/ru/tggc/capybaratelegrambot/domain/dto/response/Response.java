package ru.tggc.capybaratelegrambot.domain.dto.response;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@FunctionalInterface
public interface Response extends Consumer<TelegramBot> {

    static <Rq extends BaseRequest<Rq, Rs>, Rs extends BaseResponse> Response ofAll(List<Rq> requests) {
        return bot -> requests.forEach(bot::execute);
    }

    @SafeVarargs
    static <Rq extends BaseRequest<Rq, Rs>, Rs extends BaseResponse> Response ofAll(Rq... requests) {
        return bot -> Arrays.stream(requests).forEach(bot::execute);
    }

    static <Rq extends BaseRequest<Rq, Rs>, Rs extends BaseResponse> Response of(BaseRequest<Rq, Rs> message) {
        return bot -> bot.execute(message);
    }

    static <T> Response of(BiConsumer<TelegramBot, T> consumer, T request) {
        return bot -> consumer.accept(bot, request);
    }

    static Response of(Consumer<TelegramBot> consumer) {
        return consumer::accept;
    }

    static Response empty() {
        return bot -> {
        };
    }

    @NotNull
    @Override
    default Response andThen(@NotNull Consumer<? super TelegramBot> after) {
        return t -> {
            accept(t);
            after.accept(t);
        };
    }
}