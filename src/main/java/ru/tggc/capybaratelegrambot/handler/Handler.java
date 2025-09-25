package ru.tggc.capybaratelegrambot.handler;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendPhoto;
import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.domain.dto.response.Response;

import java.util.List;
import java.util.Optional;

public abstract class Handler {

    public Response sendSimplePhoto(PhotoDto photo) {
        long chatId = Long.parseLong(photo.getChatId());
        SendPhoto sendPhoto = new SendPhoto(chatId, photo.getUrl());
        sendPhoto.caption(photo.getCaption());
        if (photo.getMarkup() != null) {
            sendPhoto.setReplyMarkup(photo.getMarkup());
        }
        return Response.ofMessage(sendPhoto);
    }

    public Response sendSimplePhotos(List<PhotoDto> photos) {
        List<SendPhoto> list = photos.stream()
                .map(p -> {
                    long chatId = Long.parseLong(p.getChatId());
                    SendPhoto sendPhoto = new SendPhoto(chatId, p.getUrl());
                    Optional.ofNullable(p.getCaption()).ifPresent(sendPhoto::caption);
                    return sendPhoto;
                })
                .toList();
        return Response.ofPhotos(list);
    }
}
