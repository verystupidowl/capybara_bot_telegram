package ru.tggc.capybaratelegrambot.handler;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageCaption;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.domain.response.Response;

import java.util.List;

import static ru.tggc.capybaratelegrambot.utils.Utils.ifPresent;

public abstract class Handler {

    public Response sendSimplePhoto(PhotoDto photo) {
        SendPhoto sp = new SendPhoto(photo.getChatId(), photo.getUrl());
        sp.caption(photo.getCaption());
        ifPresent(photo.getMarkup(), sp::replyMarkup);
        return Response.of(sp);
    }

    public Response sendSimplePhotos(List<PhotoDto> photos) {
        List<SendPhoto> list = photos.stream()
                .map(p -> {
                    SendPhoto sp = new SendPhoto(p.getChatId(), p.getUrl());
                    ifPresent(p.getCaption(), sp::caption);
                    ifPresent(p.getMarkup(), sp::replyMarkup);
                    return sp;
                })
                .toList();
        return Response.ofAll(list);
    }

    public Response sendSimpleMessage(long chatId, String text) {
        return sendSimpleMessage(chatId, text, null);
    }

    public Response sendSimpleMessage(long chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage sm = new SendMessage(chatId, text);
        ifPresent(markup, sm::replyMarkup);
        return Response.of(sm);
    }

    public Response sendSimpleMessages(long chatId, List<String> texts) {
        List<SendMessage> responses = texts.stream()
                .map(text -> new SendMessage(chatId, text))
                .toList();
        return Response.ofAll(responses);
    }

    public Response editMessageCaption(long chatId, Integer messageId, String caption, InlineKeyboardMarkup markup) {
        EditMessageCaption emc = new EditMessageCaption(chatId, messageId);
        emc.caption(caption);
        ifPresent(markup, emc::replyMarkup);
        return Response.of(emc);
    }

    public Response editMessageCaption(List<PhotoDto> photos, Integer messageId) {
        PhotoDto first = photos.getFirst();
        EditMessageCaption emc = new EditMessageCaption(first.getChatId(), messageId);
        emc.caption(first.getCaption());
        ifPresent(first.getMarkup(), emc::replyMarkup);
        Response response = Response.of(emc);
        List<SendPhoto> sendPhotos = photos.stream()
                .map(p -> {
                    SendPhoto sp = new SendPhoto(p.getChatId(), p.getUrl());
                    ifPresent(p.getMarkup(), sp::replyMarkup);
                    ifPresent(p.getCaption(), sp::caption);
                    return sp;
                })
                .toList();
        return response.andThen(Response.ofAll(sendPhotos));
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
        return Response.ofAll(photosToSend).andThen(Response.of(dm));
    }

    public Response editPhoto(long chatId, Integer messageId, String photoUrl, String caption) {
        DeleteMessage dm = new DeleteMessage(chatId, messageId);
        return sendSimplePhoto(PhotoDto.builder()
                .url(photoUrl)
                .caption(caption)
                .chatId(chatId)
                .build()).andThen(Response.of(dm));
    }

    public Response editSimpleMessage(long chatId, int messageId, String text) {
        return editSimpleMessage(chatId, messageId, text, null);
    }

    public Response editSimpleMessage(long chatId, int messageId, String text, InlineKeyboardMarkup markup) {
        EditMessageText sm = new EditMessageText(chatId, messageId, text);
        ifPresent(markup, sm::replyMarkup);
        return Response.of(sm);
    }
}
