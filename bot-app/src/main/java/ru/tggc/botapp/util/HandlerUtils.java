package ru.tggc.botapp.util;

import com.pengrad.telegrambot.model.Message;
import lombok.experimental.UtilityClass;
import ru.tggc.botapp.exceptions.CapybaraException;
import ru.tggc.botapp.formatter.msgkey.CommonMsgKey;
import ru.tggc.botapp.keyboard.KeyboardType;
import ru.tggc.telegrambotcore.dto.DialogSession;
import ru.tggc.telegrambotcore.formatter.FormatService;
import ru.tggc.telegrambotcore.keyboard.KeyboardFactory;

import java.util.function.Consumer;

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

    public Consumer<DialogSession> fallback(FormatService formatService, KeyboardFactory keyboardFactory) {
        return prev -> {
            throw new CapybaraException(
                    formatService.get(CommonMsgKey.ALREADY_DOING, prev.state().getLabel()),
                    keyboardFactory.getKeyboardInline(KeyboardType.NOT_CHANGE)
            );
        };
    }
}
