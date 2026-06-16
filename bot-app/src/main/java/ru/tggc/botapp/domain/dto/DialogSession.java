package ru.tggc.botapp.domain.dto;

import ru.tggc.botapp.util.HistoryType;

import java.util.Map;

public record DialogSession(
        HistoryType state,
        Map<String, String> data
) {}
