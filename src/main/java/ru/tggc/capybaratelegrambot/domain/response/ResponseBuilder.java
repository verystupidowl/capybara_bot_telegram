package ru.tggc.capybaratelegrambot.domain.response;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.DeleteMessage;
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
        return message(text, null);
    }

    public ResponseBuilder message(String text, InlineKeyboardMarkup markup) {
        actions.add(bot -> {
            SendMessage sendMessage = new SendMessage(chatId, text);
            ifPresent(markup, sendMessage::setReplyMarkup);
            bot.execute(sendMessage);
        });
        return this;
    }

    public ResponseBuilder messages(Collection<String> messages) {
        messages.forEach(this::message);
        return this;
    }

    public ResponseBuilder photo(PhotoDto photo) {
        actions.add(bot -> {
            SendPhoto sp = new SendPhoto(photo.getChatId(), photo.getUrl());
            ifPresent(photo.getCaption(), sp::setCaption);
            ifPresent(photo.getMarkup(), sp::replyMarkup);
            bot.execute(sp);
        });
        return this;
    }

    public ResponseBuilder photos(Collection<PhotoDto> photos) {
        photos.forEach(p -> {
            SendPhoto sp = new SendPhoto(p.getChatId(), p.getUrl());
            ifPresent(p.getMarkup(), sp::replyMarkup);
            ifPresent(p.getCaption(), sp::caption);
            actions.add(bot -> bot.execute(sp));
        });
        return this;
    }

    public ResponseBuilder edit(int messageId, String text) {
        return edit(messageId, text, null);
    }

    public ResponseBuilder edit(List<PhotoDto> photos, Integer messageId) {
        PhotoDto first = photos.getFirst();
        actions.add(bot -> bot.execute(new DeleteMessage(first.getChatId(), messageId)));
        return photos(photos);
    }

    public ResponseBuilder edit(int messageId, String newText, InlineKeyboardMarkup markup) {
        actions.add(bot -> {
            EditMessageText ed = new EditMessageText(chatId, messageId, newText);
            ifPresent(markup, ed::replyMarkup);
            bot.execute(ed);
        });
        return this;
    }

    public ResponseBuilder editPhoto(Integer messageId, String photoUrl, String caption) {
        actions.add(bot -> bot.execute(new DeleteMessage(chatId, messageId)));
        PhotoDto photo = PhotoDto.builder()
                .url(photoUrl)
                .caption(caption)
                .chatId(chatId)
                .build();
        return photo(photo);
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
