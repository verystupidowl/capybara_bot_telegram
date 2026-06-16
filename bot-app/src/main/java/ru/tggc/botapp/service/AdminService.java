package ru.tggc.botapp.service;

import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.tggc.botapp.domain.dto.AdminStats;
import ru.tggc.botapp.repository.CapybaraRepository;
import ru.tggc.botapp.repository.ChatRepository;
import ru.tggc.botapp.repository.UserRepository;
import ru.tggc.telegrambotframework.dto.Response;
import ru.tggc.telegrambotframework.service.TelegramBotSender;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final CapybaraRepository capybaraRepository;
    private final ChatRepository chatRepository;
    private final TelegramBotSender telegramBotSender;

    public AdminStats getStats() {
        return new AdminStats(
                userRepository.count(),
                userRepository.countByBlocked(true),
                capybaraRepository.count()
        );
    }

    @Async
    public void startBroadcast(long chatId, String text) {
        CompletableFuture.runAsync(() -> {
            List<Long> chatIds = chatRepository.findAllChatIds();
            chatIds.forEach(cId -> {
                telegramBotSender.send(Response.of(new SendMessage(cId.longValue(), text)));
                try {
                    Thread.sleep(50);
                } catch (InterruptedException _) {
                    //ignore
                }
            });
        }).thenRun(() -> telegramBotSender.send(Response.of(new SendMessage(chatId, "Закончено"))));
    }
}
