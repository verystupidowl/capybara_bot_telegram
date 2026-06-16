package ru.tggc.telegrambotframework.service;

import ru.tggc.telegrambotframework.dto.UpdateContext;

public interface HistoryService {

    boolean contains(UpdateContext ctx);
}
