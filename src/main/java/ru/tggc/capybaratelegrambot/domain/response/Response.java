package ru.tggc.capybaratelegrambot.domain.response;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;
import ru.tggc.capybaratelegrambot.exceptions.SendException;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@FunctionalInterface
public interface Response extends Consumer<TelegramBot> {

    static <Rq extends BaseRequest<Rq, Rs>, Rs extends BaseResponse> Response ofAll(List<Rq> requests) {
        return bot -> requests.forEach(request -> {
            Rs rs = bot.execute(request);
            checkResponse(rs);
        });
    }

    @SafeVarargs
    static <Rq extends BaseRequest<Rq, Rs>, Rs extends BaseResponse> Response ofAll(Rq... requests) {
        return bot -> Arrays.stream(requests).forEach(request -> {
            Rs rs = bot.execute(request);
            checkResponse(rs);
        });
    }

    static <Rq extends BaseRequest<Rq, Rs>, Rs extends BaseResponse> Response of(BaseRequest<Rq, Rs> request) {
        return bot -> {
            Rs rs = bot.execute(request);
            checkResponse(rs);
        };
    }

    static Response of(Consumer<TelegramBot> consumer) {
        return consumer::accept;
    }

    static Response ofAllConsumers(List<Consumer<TelegramBot>> consumers) {
        return bot -> consumers.forEach(c -> c.accept(bot));
    }

    static <T> Response of(BiConsumer<TelegramBot, T> consumer, T request) {
        return bot -> consumer.accept(bot, request);
    }

    static Response empty() {
        return _ -> {
        };
    }

    default Response andThen(Response after) {
        return bot -> {
            this.accept(bot);
            after.accept(bot);
        };
    }

    static <Rs extends BaseResponse> void checkResponse(Rs rs) {
        if (!rs.isOk()) {
            if (rs.description() != null && !rs.description().contains("Too Many Requests")) {
                throw new SendException("Exception while sending request " + rs.description());
            }
        }
    }
}