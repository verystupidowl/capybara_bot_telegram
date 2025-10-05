package ru.tggc.capybaratelegrambot.domain.sm;

public enum BossFightEvents {
    PLAYERS_CHOSE,
    BOSS_DONE,            // босс сделал ход
    PLAYERS_DONE,         // все игроки сходили
    SHOW_MESSAGE,
    TURN_FINISHED, BATTLE_FINISHED       // проверка конца боя
}
