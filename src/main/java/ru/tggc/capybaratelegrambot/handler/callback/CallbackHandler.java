package ru.tggc.capybaratelegrambot.handler.callback;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageCaption;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import ru.tggc.capybaratelegrambot.domain.dto.response.Response;
import ru.tggc.capybaratelegrambot.handler.Handler;

import java.util.List;

@Slf4j
public abstract class CallbackHandler extends Handler {

    public Response sendSimpleMessage(String chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage sendMessage = new SendMessage(chatId, text);
        if (markup != null) {
            sendMessage.replyMarkup(markup);
        }
        return Response.ofMessages(List.of(sendMessage));
    }

    public Response sendSimpleMessages(String chatId, List<String> texts) {
        List<SendMessage> responses = texts.stream()
                .map(text -> new SendMessage(chatId, text))
                .toList();
        return Response.ofMessages(responses);
    }

    public Response editMessageCaption(String chatId, Integer messageId, String caption, InlineKeyboardMarkup markup) {
        EditMessageCaption emc = new EditMessageCaption(chatId, messageId);
        emc.caption(caption);
        if (markup != null) {
            emc.replyMarkup(markup);
        }
        return Response.ofMessage(emc);
    }

    public Response editSimpleMessage(CallbackQuery query, String text, InlineKeyboardMarkup markup) {
        String chatId = query.maybeInaccessibleMessage().chat().id().toString();
        int messageId = Integer.parseInt(query.inlineMessageId());
        return editSimpleMessage(chatId, messageId, text, markup);
    }

    public Response editSimpleMessage(String chatId, int messageId, String text) {
        return editSimpleMessage(chatId, messageId, text, null);
    }

    public Response editSimpleMessage(String chatId, int messageId, String text, InlineKeyboardMarkup markup) {
        EditMessageText sendMessage = new EditMessageText(chatId, messageId, text);
        if (markup != null) {
            sendMessage.replyMarkup(markup);
        }
        return Response.ofEditTexts(List.of(sendMessage));
    }
}
