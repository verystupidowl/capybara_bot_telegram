package ru.tggc.capybaratelegrambot.handler.callback;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageCaption;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import lombok.extern.slf4j.Slf4j;
import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.domain.dto.response.Response;
import ru.tggc.capybaratelegrambot.handler.Handler;

import java.util.List;

import static ru.tggc.capybaratelegrambot.utils.Utils.ifPresent;

@Slf4j
public abstract class CallbackHandler extends Handler {

    public Response sendSimpleMessage(long chatId, String text) {
        return sendSimpleMessage(chatId, text, null);
    }

    public Response sendSimpleMessage(long chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage sm = new SendMessage(chatId, text);
        ifPresent(markup, sm::replyMarkup);
        return Response.ofMessages(sm);
    }

    public Response sendSimpleMessages(long chatId, List<String> texts) {
        List<SendMessage> responses = texts.stream()
                .map(text -> new SendMessage(chatId, text))
                .toList();
        return Response.ofMessages(responses);
    }

    public Response editMessageCaption(long chatId, Integer messageId, String caption, InlineKeyboardMarkup markup) {
        EditMessageCaption emc = new EditMessageCaption(chatId, messageId);
        emc.caption(caption);
        ifPresent(markup, emc::replyMarkup);
        return Response.ofMessage(emc);
    }

    public Response editMessageCaption(List<PhotoDto> photos, Integer messageId) {
        PhotoDto first = photos.getFirst();
        EditMessageCaption emc = new EditMessageCaption(first.getChatId(), messageId);
        emc.caption(first.getCaption());
        ifPresent(first.getMarkup(), emc::replyMarkup);
        Response response = Response.ofMessage(emc);
        List<SendPhoto> sendPhotos = photos.stream()
                .map(p -> {
                    SendPhoto sp = new SendPhoto(p.getChatId(), p.getUrl());
                    ifPresent(p.getMarkup(), sp::replyMarkup);
                    ifPresent(p.getCaption(), sp::caption);
                    return sp;
                })
                .toList();
        return response.andThen(Response.ofPhotos(sendPhotos));
    }

    public Response editPhotos(Integer messageId, List<PhotoDto> photos) {
        PhotoDto first = photos.getFirst();
        DeleteMessage dm = new DeleteMessage(first.getChatId(), messageId);
        List<SendPhoto> photosToSend = photos.stream()
                .map(p -> {
                    long chatId = p.getChatId();
                    SendPhoto sp = new SendPhoto(chatId, p.getUrl());
                    ifPresent(p.getCaption(), sp::caption);
                    ifPresent(p.getMarkup(), sp::replyMarkup);
                    return sp;
                })
                .toList();
        return Response.ofPhotos(photosToSend).andThen(Response.ofMessage(dm));
    }

    public Response editPhoto(long chatId, Integer messageId, String photoUrl, String caption) {
        DeleteMessage dm = new DeleteMessage(chatId, messageId);
        return sendSimplePhoto(PhotoDto.builder()
                .url(photoUrl)
                .caption(caption)
                .chatId(chatId)
                .build()).andThen(bot -> bot.execute(dm));
    }

    public Response editSimpleMessage(CallbackQuery query, String text, InlineKeyboardMarkup markup) {
        long chatId = query.maybeInaccessibleMessage().chat().id();
        int messageId = Integer.parseInt(query.inlineMessageId());
        return editSimpleMessage(chatId, messageId, text, markup);
    }

    public Response editSimpleMessage(long chatId, int messageId, String text) {
        return editSimpleMessage(chatId, messageId, text, null);
    }

    public Response editSimpleMessage(long chatId, int messageId, String text, InlineKeyboardMarkup markup) {
        EditMessageText sm = new EditMessageText(chatId, messageId, text);
        ifPresent(markup, sm::replyMarkup);
        return Response.ofEditTexts(List.of(sm));
    }
}
