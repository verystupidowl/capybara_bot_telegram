package ru.tggc.capybaratelegrambot.utils;

public enum SlotResult {
    JACKPOT(5.0),     // три "семь"
    TRIPLE(3.0),      // три одинаковых
    DOUBLE(1.85),     // первые два одинаковых
    LOSE(-1.0);       // проигрыш

    private final double multiplier;


    SlotResult(double multiplier) {
        this.multiplier = multiplier;
    }

    public double multiplier() {
        return multiplier;
    }
}
