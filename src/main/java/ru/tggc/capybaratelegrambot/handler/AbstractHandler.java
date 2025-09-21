package ru.tggc.capybaratelegrambot.handler;

import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.sender.Sender;

public abstract class AbstractHandler<H> implements Handler<H> {
    private final Sender sender;

    protected AbstractHandler(Sender sender) {
        this.sender = sender;
    }

    public <T extends BaseRequest<T, R>, R extends BaseResponse> MessageWrapper<T, R> create(T method) {
        MessageWrapper<T, R> methodWrapper = new MessageWrapper<>(sender);
        methodWrapper.setRequest(method);
        return methodWrapper;
    }

    public <T extends BaseRequest<T, R>, R extends BaseResponse> MessageWrapper<T, R> create(T method, String s) {
        MessageWrapper<T, R> methodWrapper = new MessageWrapper<>(sender);
        methodWrapper.setRequest(method);
        return methodWrapper;
    }

    public <T extends BaseRequest<T, R>, R extends BaseResponse> MessageWrapper<T, R> create(T method, Integer s) {
        MessageWrapper<T, R> methodWrapper = new MessageWrapper<>(sender);
        methodWrapper.setRequest(method);
        return methodWrapper;
    }

    public SendResponse sendSimplePhoto(PhotoDto photo) {
        SendPhoto sendPhoto = new SendPhoto(photo.getChatId(), photo.getUrl());
        return create(sendPhoto).send();
    }
}
