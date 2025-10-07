package ru.tggc.capybaratelegrambot.domain.sm.state;

public enum BossFightStates {
    WAITING_FOR_PLAYERS,  // ждём, пока все игроки выберут действие
    PLAYER_TURN,          // ход игроков
    BOSS_TURN,            // ход босса
    SEND_MESSAGE, END_BATTLE
}
