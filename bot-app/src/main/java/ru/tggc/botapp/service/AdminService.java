package ru.tggc.botapp.service;

import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.botapp.domain.model.BlockInfo;
import ru.tggc.botapp.domain.model.User;
import ru.tggc.botapp.exceptions.CapybaraException;
import ru.tggc.botapp.exceptions.UserNotFoundException;
import ru.tggc.botapp.formatter.msgkey.AdminMsgKey;
import ru.tggc.botapp.formatter.msgkey.CommonMsgKey;
import ru.tggc.botapp.formatter.msgkey.ErrorMsgKey;
import ru.tggc.botapp.keyboard.KeyboardType;
import ru.tggc.botapp.repository.BlockRepository;
import ru.tggc.botapp.repository.CapybaraRepository;
import ru.tggc.botapp.repository.ChatRepository;
import ru.tggc.botapp.repository.UserRepository;
import ru.tggc.botapp.util.HistoryType;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.formatter.FormatService;
import ru.tggc.telegrambotcore.keyboard.KeyboardFactory;
import ru.tggc.telegrambotcore.service.HistoryService;
import ru.tggc.telegrambotcore.service.TelegramBotSender;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final CapybaraRepository capybaraRepository;
    private final ChatRepository chatRepository;
    private final BlockRepository blockRepository;
    private final TelegramBotSender telegramBotSender;
    private final FormatService formatService;
    private final KeyboardFactory keyboardFactory;
    private final HistoryService historyService;

    @Transactional(readOnly = true)
    public Response getStats(UpdateContext ctx) {
        long userCount = userRepository.count();
        long blockedUsers = blockRepository.count();
        long capybaraCount = capybaraRepository.count();
        String messageToSend = formatService.get(AdminMsgKey.STATS, userCount, blockedUsers, capybaraCount);

        return ctx.send(
                messageToSend,
                keyboardFactory.getKeyboardInline(KeyboardType.ADMIN_MENU)
        );
    }

    public Response broadcast(UpdateContext ctx) {
        historyService.setHistory(ctx, HistoryType.BROADCAST, prev -> {
            throw new CapybaraException(
                    formatService.get(CommonMsgKey.ALREADY_DOING, prev.state().getLabel()),
                    keyboardFactory.getKeyboardInline(KeyboardType.NOT_CHANGE)
            );
        });
        return ctx.send(formatService.get(AdminMsgKey.BROADCAST_START));
    }

    @Async
    public void startBroadcast(long chatId, String text) {
        List<Long> chatIds = chatRepository.findAllChatIds();
        CompletableFuture.runAsync(() -> chatIds.forEach(cId -> {
            telegramBotSender.send(Response.of(new SendMessage(cId.longValue(), text)));
            try {
                Thread.sleep(50);
            } catch (InterruptedException _) {
                //ignore
            }
        })).thenRun(() -> {
            String message = formatService.get(AdminMsgKey.BROADCAST_ENDED, chatIds.size());
            telegramBotSender.send(Response.of(new SendMessage(chatId, message)));
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Response blockUser(String username, String reason, String reporterUsername, UpdateContext ctx) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    String message = formatService.get(ErrorMsgKey.USER_USERNAME_NOT_FOUND, username);
                    return new UserNotFoundException(message);
                });
        User reporter = userRepository.findByUsername(reporterUsername)
                .orElseThrow(() -> {
                    String message = formatService.get(ErrorMsgKey.USER_USERNAME_NOT_FOUND, reporterUsername);
                    return new UserNotFoundException(message);
                });

        BlockInfo blockInfo = new BlockInfo();
        blockInfo.setReason(reason);
        blockInfo.setUser(user);
        blockInfo.setReporter(reporter);
        blockRepository.save(blockInfo);

        return ctx.send(formatService.get(AdminMsgKey.BLOCK_MESSAGE, username, reporterUsername, reason));
    }
}
