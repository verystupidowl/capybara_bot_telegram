package ru.tggc.telegrambotframework.formatter;

public interface FormatService {

    String getMessage(MsgKey key, Object... args);
}
