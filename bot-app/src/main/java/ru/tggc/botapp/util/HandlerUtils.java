package ru.tggc.botapp.util;

import com.pengrad.telegrambot.model.Message;
import lombok.experimental.UtilityClass;
import ru.tggc.botapp.exceptions.CapybaraException;

@UtilityClass
public class HandlerUtils {

    public String getTargetUsername(String username, Message message) {
        if (username == null && message.replyToMessage() != null) {
            return message.replyToMessage().from().username();
        } else if (username == null && message.replyToMessage() == null) {
            throw new CapybaraException("Ответь на сообщение");
        }
        return username;
    }
}
