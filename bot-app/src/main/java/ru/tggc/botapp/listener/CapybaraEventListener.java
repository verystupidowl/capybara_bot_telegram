package ru.tggc.botapp.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.tggc.botapp.listener.event.NewLevelEvent;
import ru.tggc.botapp.listener.event.NewTypeEvent;
import ru.tggc.telegrambotcore.service.TelegramBotSender;

@Component
@RequiredArgsConstructor
public class CapybaraEventListener {
    private final TelegramBotSender sender;

    @EventListener
    public void newLevelListener(NewLevelEvent event) {
        sender.send(event.response());
    }

    @EventListener
    public void NewTypeListener(NewTypeEvent event) {
        sender.send(event.response());
    }
}
