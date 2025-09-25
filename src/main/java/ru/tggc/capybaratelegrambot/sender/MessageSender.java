package ru.tggc.capybaratelegrambot.sender;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageSender implements Sender {
    private final TelegramBot bot;

    @Override
    public <T extends BaseRequest<T, R>, R extends BaseResponse> R send(BaseRequest<T, R> request) {
        return bot.execute(request);
    }
}
