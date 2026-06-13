package ru.tggc.capybaratelegrambot.router;

import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.domain.response.Response;
import ru.tggc.capybaratelegrambot.registry.HandleRegistry;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramUpdateRouter {
    private final List<HandleRegistry> handlers;

    public Response route(Update update) {
        return handlers.stream()
                .filter(handler -> handler.canHandle(update))
                .findFirst()
                .map(registry -> registry.dispatch(update))
                .orElse(Response.empty());
    }
}
