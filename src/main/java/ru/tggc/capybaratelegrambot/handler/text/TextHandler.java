package ru.tggc.capybaratelegrambot.handler.text;

import com.pengrad.telegrambot.model.Message;
import lombok.extern.slf4j.Slf4j;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;
import ru.tggc.capybaratelegrambot.handler.Handler;

@Slf4j
public abstract class TextHandler extends Handler {

    protected String getTargetUsername(String username, Message message) {
        if (username == null && message.replyToMessage() != null) {
            return message.replyToMessage().from().username();
        } else if (username == null && message.replyToMessage() == null) {
            throw new CapybaraException("Ответь на сообщение");
        }
        return username;
    }
}
