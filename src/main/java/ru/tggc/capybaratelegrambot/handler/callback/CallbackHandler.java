package ru.tggc.capybaratelegrambot.handler.callback;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.extern.slf4j.Slf4j;
import ru.tggc.capybaratelegrambot.domain.response.Response;
import ru.tggc.capybaratelegrambot.handler.Handler;

@Slf4j
public abstract class CallbackHandler extends Handler {

    public Response editSimpleMessage(CallbackQuery query, String text, InlineKeyboardMarkup markup) {
        long chatId = query.maybeInaccessibleMessage().chat().id();
        int messageId = Integer.parseInt(query.inlineMessageId());
        return editSimpleMessage(chatId, messageId, text, markup);
    }
}
