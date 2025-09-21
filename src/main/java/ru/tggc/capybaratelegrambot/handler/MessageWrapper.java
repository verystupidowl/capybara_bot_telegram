package ru.tggc.capybaratelegrambot.handler;

import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.sender.Sender;

@Service
@RequiredArgsConstructor
@Setter
public class MessageWrapper<T extends BaseRequest<T, R>, R extends BaseResponse> {
    private BaseRequest<T, R> request;
    private final Sender sender;

    public R send() {
        return sender.send(request);
    }

}
