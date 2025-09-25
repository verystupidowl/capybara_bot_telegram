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

    public Response sendSimpleMessage(String chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage sendMessage = new SendMessage(chatId, text);
        ifPresent(markup, sendMessage::replyMarkup);
        return Response.ofMessages(List.of(sendMessage));
    }

    public Response sendSimpleMessages(String chatId, List<String> texts) {
        List<SendMessage> responses = texts.stream()
                .map(text -> new SendMessage(chatId, text))
                .toList();
        return Response.ofMessages(responses);
    }

    public Response editMessageCaption(String chatId, Integer messageId, String caption, InlineKeyboardMarkup markup) {
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
        List<SendPhoto> sendPhotos = photos.stream().map(p -> {
                    long chatId = Long.parseLong(p.getChatId());
                    return new SendPhoto(chatId, p.getUrl());
                })
                .toList();
        return response.andThen(Response.ofPhotos(sendPhotos));
    }

    public Response editPhotos(Integer messageId, List<PhotoDto> photos) {
        PhotoDto first = photos.getFirst();
        DeleteMessage dm = new DeleteMessage(first.getChatId(), messageId);
        List<SendPhoto> photosToSend = photos.stream()
                .map(p -> {
                    long chatId = Long.parseLong(p.getChatId());
                    SendPhoto sendPhoto = new SendPhoto(chatId, p.getUrl());
                    ifPresent(p.getCaption(), sendPhoto::caption);
                    ifPresent(p.getMarkup(), sendPhoto::replyMarkup);
                    return sendPhoto;
                })
                .toList();
        return Response.ofPhotos(photosToSend).andThen(Response.ofMessage(dm));
    }

    public Response editPhoto(String chatId, Integer messageId, String photoUrl, String caption) {
        DeleteMessage dm = new DeleteMessage(chatId, messageId);
        return sendSimplePhoto(PhotoDto.builder()
                .url(photoUrl)
                .caption(caption)
                .chatId(chatId)
                .build()).andThen(bot -> bot.execute(dm));
    }

    public Response editSimpleMessage(CallbackQuery query, String text, InlineKeyboardMarkup markup) {
        String chatId = query.maybeInaccessibleMessage().chat().id().toString();
        int messageId = Integer.parseInt(query.inlineMessageId());
        return editSimpleMessage(chatId, messageId, text, markup);
    }

    public Response editSimpleMessage(String chatId, int messageId, String text) {
        return editSimpleMessage(chatId, messageId, text, null);
    }

    public Response editSimpleMessage(String chatId, int messageId, String text, InlineKeyboardMarkup markup) {
        EditMessageText sendMessage = new EditMessageText(chatId, messageId, text);
        if (markup != null) {
            sendMessage.replyMarkup(markup);
        }
        return Response.ofEditTexts(List.of(sendMessage));
    }
}
