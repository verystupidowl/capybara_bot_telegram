package ru.tggc.capybaratelegrambot.service;

import com.pengrad.telegrambot.model.CallbackQuery;
import ru.tggc.capybaratelegrambot.utils.CasinoTargetType;
import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.domain.dto.TopCapybaraDto;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.enums.ImprovementValue;
import ru.tggc.capybaratelegrambot.domain.model.enums.JobType;
import ru.tggc.capybaratelegrambot.handler.callback.CallbackHandler;

import java.util.List;
import java.util.function.BiConsumer;

public interface CapybaraService {
    Capybara getCapybara(Long id, String chatId);

    Capybara getCapybaraByUserId(String userId, String chatId);

    String setDefaultPhoto(String userId, String chatId);

    List<PhotoDto> makeHappy(String userId, String chatId);

    List<PhotoDto> feed(String userId, String chatId);

    List<PhotoDto> fatten(String userId, String chatId);

    List<PhotoDto> goTea(String userId, String chatId);

    void takeFromTea(String userId, String chatId);

    PhotoDto saveCapybara(String userId, String chatId);

    boolean hasWork(String userId, String chatId);

    void setJob(String userId, String chatId, JobType jobType);

    void goJob(String userId, String chatId);

    List<String> takeFromWork(String userId, String chatId);

    BiConsumer<CallbackHandler, CallbackQuery> acceptRace(String userId, String chatId);

    void refuseRace(String userId, String chatId);

    void doMassage(String userId, String chatId);

    void setImprovement(String userId, String chatId, ImprovementValue improvementValue);

    void acceptWedding(String userId, String chatId);

    void deleteCapybara(String userId, String chatId);

    List<TopCapybaraDto> getTopCapybaras();

    void dismissal(String userId, String chatId);

    String casino(String userId, String chatId, long count, CasinoTargetType type);

    void transferMoney(String userId, String chatId, String targetUsername, Long amount);
}
