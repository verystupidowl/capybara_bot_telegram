package ru.tggc.botapp.util;

public enum SlotResult {
    JACKPOT(5.0),
    TRIPLE(3.0),
    DOUBLE(1.85),
    LOSE(0.0);

    private final double multiplier;


    SlotResult(double multiplier) {
        this.multiplier = multiplier;
    }

    public double multiplier() {
        return multiplier;
    }
}
