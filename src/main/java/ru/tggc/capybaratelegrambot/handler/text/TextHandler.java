package ru.tggc.capybaratelegrambot.handler.text;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.tggc.capybaratelegrambot.domain.dto.response.Response;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;
import ru.tggc.capybaratelegrambot.handler.Handler;

import static ru.tggc.capybaratelegrambot.utils.Utils.ifPresent;

@Slf4j
public abstract class TextHandler extends Handler {

    protected Response sendSimpleMessage(long chatId,
                                         @NotNull String text,
                                         @Nullable Keyboard markup) {
        SendMessage sm = new SendMessage(chatId, text);
        ifPresent(markup, sm::replyMarkup);
        return Response.ofMessage(sm);
    }

    protected Response sendSimpleMessage(long chatId, @NotNull String text) {
        return sendSimpleMessage(chatId, text, null);
    }

    protected String getTargetUsername(String username, Message message) {
        if (username == null && message.replyToMessage() != null) {
            return message.replyToMessage().from().username();
        } else if (username == null && message.replyToMessage() == null) {
            throw new CapybaraException("Ответь на сообщение");
        }
        return username;
    }
}
