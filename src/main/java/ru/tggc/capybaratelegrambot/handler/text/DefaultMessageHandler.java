package ru.tggc.capybaratelegrambot.handler.text;

import com.pengrad.telegrambot.model.Message;
import lombok.RequiredArgsConstructor;
import ru.tggc.capybaratelegrambot.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.annotation.handle.DefaultMessageHandle;
import ru.tggc.capybaratelegrambot.annotation.params.MessageParam;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.domain.response.Response;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.service.CapybaraService;
import ru.tggc.capybaratelegrambot.service.CasinoService;
import ru.tggc.capybaratelegrambot.service.HistoryService;
import ru.tggc.capybaratelegrambot.service.RaceService;
import ru.tggc.capybaratelegrambot.utils.HistoryType;

@BotHandler
@RequiredArgsConstructor
public class DefaultMessageHandler extends TextHandler {
    private final HistoryService historyService;
    private final CasinoService casinoService;
    private final InlineKeyboardCreator inlineCreator;
    private final CapybaraService capybaraService;
    private final RaceService raceService;

    @DefaultMessageHandle
    public Response handleDefaultMessages(@MessageParam Message message) {
        long chatId = message.chat().id();
        long userId = message.from().id();
        String text = message.text();
        CapybaraContext historyDto = new CapybaraContext(chatId, userId, message.messageId());
        HistoryType historyType = historyService.getFromHistory(historyDto);
        if (historyType == null) {
            return null;
        }

        Response response = switch (historyType) {
            case CASINO_SET_BET -> casinoSetBet(historyDto, text);
            case CHANGE_NAME -> changeName(historyDto, text);
            case SLOTS_SET_BET -> slots(historyDto, text);
            case START_RACE -> race(historyDto, text);
            default -> null;
        };

        historyService.removeFromHistory(historyDto);
        return response;
    }

    private Response race(CapybaraContext ctx, String text) {
        if (!text.startsWith("@")) {
            return null;
        }
        String username = text.substring(1);
        raceService.sendRequest(username, ctx);
        historyService.removeFromHistory(ctx);
        return sendSimpleMessage(ctx.chatId(), text + ", тебе бросили вызов!", inlineCreator.raceKeyboard());
    }

    private Response slots(CapybaraContext historyDto, String bet) {
        return casinoService.slots(historyDto, Long.parseLong(bet));
    }

    private Response changeName(CapybaraContext historyDto, String text) {
        capybaraService.changeName(historyDto, text);
        return sendSimpleMessage(historyDto.chatId(), "Твою капибару теперь зовут " + text + ", отличное имя!");
    }

    private Response casinoSetBet(CapybaraContext historyDto, String text) {
        casinoService.setBet(historyDto, text);
        PhotoDto photoDto = PhotoDto.builder()
                .caption("Введите цель")
                .url("https://vk.com/photo-209917797_457246196")
                .chatId(historyDto.chatId())
                .markup(inlineCreator.casinoTargetKeyboard())
                .build();
        return sendSimplePhoto(photoDto);
    }
}
