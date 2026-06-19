package ru.tggc.botapp.formatter;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import ru.tggc.botapp.formatter.msgkey.MsgKey;

@Component
@RequiredArgsConstructor
public class FormatService {
    private final MessageSource messageSource;

    public String getMsg(String key, Object... args) {
        return messageSource.getMessage(key, args, null);
    }

    public String get(MsgKey key, Object... args) {
        return messageSource.getMessage(key.getKey(), args, null);
    }
}
