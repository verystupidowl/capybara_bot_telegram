package ru.tggc.capybaratelegrambot.handler;

import com.pengrad.telegrambot.request.SendPhoto;
import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.domain.dto.response.Response;
import ru.tggc.capybaratelegrambot.utils.Utils;

import java.util.List;

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
                    Utils.ifPresent(p.getCaption(), sendPhoto::caption);
                    Utils.ifPresent(p.getMarkup(), sendPhoto::replyMarkup);
                    return sendPhoto;
                })
                .toList();
        return Response.ofPhotos(list);
    }
}
