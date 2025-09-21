package ru.tggc.capybaratelegrambot.service;

import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;

public interface WeddingService {

    PhotoDto respondWedding(String userId, String chatId, boolean accept);

    String respondUnWedding(String userId, String chatId, boolean accept);
}
