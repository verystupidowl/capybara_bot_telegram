package ru.tggc.botapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tggc.botapp.formatter.msgkey.CommonMsgKey;
import ru.tggc.telegrambotcore.dto.PhotoDto;
import ru.tggc.telegrambotcore.formatter.FormatService;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonService {
    @Value("${bot.photos.start}")
    private String startPhoto;

    private final FormatService formatService;

    public PhotoDto start(Long chatId) {
        return new PhotoDto(
                startPhoto,
                formatService.getMessage(CommonMsgKey.START_MESSAGE),
                chatId
        );
    }
}
