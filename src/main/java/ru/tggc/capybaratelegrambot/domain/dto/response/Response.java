package ru.tggc.capybaratelegrambot.domain.dto.response;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.BaseResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@FunctionalInterface
public interface Response extends Consumer<TelegramBot> {

    static Response ofMessages(List<SendMessage> messages) {
        return bot -> messages.forEach(bot::execute);
    }

    static Response ofMessages(SendMessage... messages) {
        return bot -> Arrays.stream(messages).forEach(bot::execute);
    }

    static Response ofEditTexts(List<EditMessageText> messages) {
        return bot -> messages.forEach(bot::execute);
    }

    static Response ofPhotos(List<SendPhoto> messages) {
        return bot -> messages.forEach(bot::execute);
    }

    static <Rq extends BaseRequest<Rq, Rs>, Rs extends BaseResponse> Response ofMessage(BaseRequest<Rq, Rs> message) {
        return bot -> bot.execute(message);
    }

    static <T> Response ofCustom(BiConsumer<TelegramBot, T> consumer, T request) {
        return bot -> consumer.accept(bot, request);
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