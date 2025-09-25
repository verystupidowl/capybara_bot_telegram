package ru.tggc.capybaratelegrambot.sender;

import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;

public interface Sender {

    <T extends BaseRequest<T, R>, R extends BaseResponse> R send(BaseRequest<T, R> request);
}
