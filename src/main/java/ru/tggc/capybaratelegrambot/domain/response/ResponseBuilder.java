package ru.tggc.capybaratelegrambot.domain.response;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.exceptions.SendException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static ru.tggc.capybaratelegrambot.utils.Utils.ifPresent;

@Component
@NoArgsConstructor
public class ResponseBuilder {
    private long chatId;
    private final List<Consumer<TelegramBot>> actions = new ArrayList<>();
    private Function<Exception, Response> exceptionHandler = null;

    private static long adminId;

    public void setAdminId(@Value("${bot.admin-id}") long adminId) {
        ResponseBuilder.adminId = adminId;
    }

    private ResponseBuilder(long chatId) {
        this.chatId = chatId;
    }

    public ResponseBuilder add(Consumer<TelegramBot> action) {
        this.actions.add(action);
        return this;
    }

    public ResponseBuilder addAll(Collection<Consumer<TelegramBot>> action) {
        this.actions.addAll(action);
        return this;
    }

    public static ResponseBuilder to(long chatId) {
        return new ResponseBuilder(chatId);
    }

    public static ResponseBuilder toAdmin() {
        return new ResponseBuilder(adminId);
    }

    public static ResponseBuilder create() {
        return new ResponseBuilder(0);
    }

    public ResponseBuilder message(String text) {
        actions.add(bot -> bot.execute(new SendMessage(chatId, text)));
        return this;
    }

    public ResponseBuilder photo(PhotoDto photo) {
        SendPhoto sp = new SendPhoto(photo.getChatId(), photo.getUrl());
        sp.caption(photo.getCaption());
        ifPresent(photo.getMarkup(), sp::replyMarkup);
        actions.add(bot -> bot.execute(sp));
        return this;
    }

    public ResponseBuilder edit(int messageId, String newText) {
        actions.add(bot -> bot.execute(new EditMessageText(chatId, messageId, newText)));
        return this;
    }

    public ResponseBuilder exceptionally(Function<Exception, Response> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    public Response build() {
        if (exceptionHandler != null) {
            try {
                return Response.ofAllConsumers(actions);
            } catch (SendException ex) {
                return exceptionHandler.apply(ex);
            }
        } else {
            return Response.ofAllConsumers(actions);
        }
    }
}
