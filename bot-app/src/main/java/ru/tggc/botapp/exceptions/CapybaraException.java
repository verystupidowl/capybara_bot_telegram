package ru.tggc.botapp.exceptions;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.Getter;
import ru.tggc.telegrambotcore.formatter.MsgKey;

@Getter
public class CapybaraException extends RuntimeException {
    private String messageToSend;
    private MsgKey msgKey;
    private InlineKeyboardMarkup markup;

    public CapybaraException(String messageToSend) {
        super(messageToSend);
        this.messageToSend = messageToSend;
    }

    public CapybaraException(String messageToSend, InlineKeyboardMarkup markup) {
        super(messageToSend);
        this.messageToSend = messageToSend;
        this.markup = markup;
    }

    public CapybaraException(String message, String messageToSend) {
        super(message);
        this.messageToSend = messageToSend;
    }

    public CapybaraException(MsgKey msgKey) {
        super(msgKey.toString());
        this.msgKey = msgKey;
    }

    public CapybaraException(MsgKey msgKey, InlineKeyboardMarkup markup) {
        super(msgKey.toString());
        this.msgKey = msgKey;
        this.markup = markup;
    }

    public boolean hasMsgKey() {
        return this.msgKey != null;
    }
}
