package ru.tggc.capybaratelegrambot.handler.callback;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.aop.CallbackRegistry;
import ru.tggc.capybaratelegrambot.handler.AbstractHandler;
import ru.tggc.capybaratelegrambot.sender.Sender;

@Slf4j
public abstract class CallbackHandler extends AbstractHandler<CallbackQuery> {
    private final CallbackRegistry callbackRegistry;

    protected CallbackHandler(Sender sender, CallbackRegistry callbackRegistry) {
        super(sender);
        this.callbackRegistry = callbackRegistry;
    }

    public void sendSimpleMessage(CallbackQuery query, String text, Keyboard markup) {
        String chatId = query.maybeInaccessibleMessage().chat().id().toString();
        int messageId = Integer.parseInt(query.inlineMessageId());
        sendSimpleMessage(chatId, messageId, text, markup);
    }

    public void sendSimpleMessage(String chatId, int messageId, String text) {
        sendSimpleMessage(chatId, messageId, text, null);
    }

    public void sendSimpleMessage(String chatId, int messageId, String text, Keyboard markup) {
        DeleteMessage deleteMessage = new DeleteMessage(chatId, messageId);
        SendMessage sendMessage = new SendMessage(chatId, text);
        if (markup != null) {
            sendMessage.replyMarkup(markup);
        }
        create(deleteMessage).send();
        create(sendMessage).send();
    }

    @Override
    public void handle(CallbackQuery data) {
        callbackRegistry.dispatch(data);
    }
}
