package ru.tggc.capybaratelegrambot.handler;

import com.pengrad.telegrambot.request.SendPhoto;
import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.domain.dto.response.Response;

import java.util.List;

import static ru.tggc.capybaratelegrambot.utils.Utils.ifPresent;

public abstract class Handler {

    public Response sendSimplePhoto(PhotoDto photo) {
        SendPhoto sp = new SendPhoto(photo.getChatId(), photo.getUrl());
        sp.caption(photo.getCaption());
        ifPresent(photo.getMarkup(), sp::replyMarkup);
        return Response.ofMessage(sp);
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
        return Response.ofPhotos(list);
    }
}
