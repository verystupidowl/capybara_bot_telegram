package ru.tggc.botapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.Chat;
import ru.tggc.botapp.domain.model.User;
import ru.tggc.botapp.exceptions.CapybaraException;
import ru.tggc.botapp.formatter.msgkey.CommonMsgKey;
import ru.tggc.botapp.repository.CapybaraRepository;
import ru.tggc.botapp.util.HistoryType;
import ru.tggc.telegrambotcore.dto.PhotoDto;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.formatter.FormatService;
import ru.tggc.telegrambotcore.service.HistoryService;
import ru.tggc.telegrambotcore.service.TelegramBotSender;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonService {
    private final HistoryService historyService;
    private final CapybaraRepository capybaraRepository;
    private final TelegramBotSender telegramBotSender;
    @Value("${bot.photos.start}")
    private String startPhoto;

    private final FormatService formatService;

    public PhotoDto start(Long chatId) {
        return new PhotoDto(
                startPhoto,
                formatService.get(CommonMsgKey.START_MESSAGE),
                chatId
        );
    }

    public String startBugReport(UpdateContext ctx) {
        historyService.setHistory(ctx, HistoryType.BUG_REPORT);
        return formatService.get(CommonMsgKey.START_BUG_REPORT);
    }

    public String bugReport(UpdateContext ctx, String text) {
        Capybara capybara = capybaraRepository.findCapybaraWithUserByUserIdAndChatId(ctx.userId(), ctx.chatId())
                .orElseThrow(() -> new CapybaraException("У тебя нет капибары"));
        User user = capybara.getUser();
        Chat chat = capybara.getChat();
        String messageToAdmin = formatService.get(
                CommonMsgKey.BUG_REPORT_TO_ADMIN,
                user.getUsername(),
                chat.getId(),
                chat.getName(),
                capybara.getId(),
                capybara.getName(),
                text
        );

        telegramBotSender.sendToAdmin(messageToAdmin);

        return formatService.get(CommonMsgKey.BUG_REPORT_THANKS);
    }
}
