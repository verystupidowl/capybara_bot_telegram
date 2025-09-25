package ru.tggc.capybaratelegrambot.utils;

public enum SlotType {
    BAR("BAR"),
    GRAPE("виноград"),
    LEMON("лимон"),
    SEVEN("семь");

    private final String label;

    SlotType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public static SlotType fromIndex(int index) {
        return values()[index % values().length];
    }
}
