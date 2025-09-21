package ru.tggc.capybaratelegrambot.service;

import com.pengrad.telegrambot.model.CallbackQuery;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.handler.callback.CallbackHandler;

import java.util.function.BiConsumer;

public interface RaceService {

    BiConsumer<CallbackHandler, CallbackQuery> respondRace(Capybara opponent, boolean accept);
}
