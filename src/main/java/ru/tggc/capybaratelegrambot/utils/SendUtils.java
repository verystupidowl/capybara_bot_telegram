package ru.tggc.capybaratelegrambot.utils;

import com.pengrad.telegrambot.model.ResponseParameters;
import com.pengrad.telegrambot.response.BaseResponse;
import lombok.experimental.UtilityClass;
import ru.tggc.capybaratelegrambot.exceptions.RetryableException;
import ru.tggc.capybaratelegrambot.exceptions.RetryableWithSecsException;

import static ru.tggc.capybaratelegrambot.utils.Utils.getOrNull;

@UtilityClass
public class SendUtils {

    public <Rs extends BaseResponse> void checkRequestAndResponse(Rs rs) {
        if (!rs.isOk()) {
            String description = rs.description();
            Integer retryAfter = getOrNull(rs.parameters(), ResponseParameters::retryAfter);

            if (retryAfter != null) {
                throw new RetryableException(description, retryAfter);
            }

            if (description != null) {
                String d = description.toLowerCase();

                if (d.contains("timeout")
                        || d.contains("temporarily unavailable")
                        || d.contains("internal server error")
                        || d.contains("bad gateway")) {

                    throw new RetryableWithSecsException(description);
                }
            }

            throw new RetryableWithSecsException(description + rs);
        }
    }
}
