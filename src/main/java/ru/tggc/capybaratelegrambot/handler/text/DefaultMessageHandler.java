package ru.tggc.capybaratelegrambot.handler.text;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendDice;
import lombok.RequiredArgsConstructor;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.DefaultMessageHandle;
import ru.tggc.capybaratelegrambot.aop.annotation.params.MessageParam;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.domain.dto.response.Response;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.service.CapybaraService;
import ru.tggc.capybaratelegrambot.service.CasinoService;
import ru.tggc.capybaratelegrambot.service.HistoryService;
import ru.tggc.capybaratelegrambot.utils.HistoryType;

@BotHandler
@RequiredArgsConstructor
public class DefaultMessageHandler extends TextHandler {
    private final HistoryService historyService;
    private final CasinoService casinoService;
    private final InlineKeyboardCreator inlineCreator;
    private final CapybaraService capybaraService;

    @DefaultMessageHandle
    public Response handleDefaultMessages(@MessageParam Message message) {
        String chatId = message.chat().id().toString();
        String userId = message.from().id().toString();
        String text = message.text();
        CapybaraContext historyDto = new CapybaraContext(chatId, userId);
        HistoryType historyType = historyService.getFromHistory(historyDto);
        if (historyType == null) {
            return null;
        }

        Response response = switch (historyType) {
            case CASINO_SET_BET -> casinoSetBet(historyDto, text);
            case CHANGE_NAME -> changeName(historyDto, text);
            case CHANGE_PHOTO -> changePhoto(historyDto, message);
            case SLOTS_SET_BET -> slots(historyDto, text);
            default-> throw new UnsupportedOperationException();
        };

        historyService.removeFromHistory(historyDto);
        return response;
    }

    private Response slots(CapybaraContext historyDto, String bet) {
        return Response.ofCustom(casinoService.slots(historyDto, Long.parseLong(bet)), historyDto);
    }

    private Response changeName(CapybaraContext historyDto, String text) {
//        capybaraService.changeName(historyDto, text);
        return null;
    }

    private Response changePhoto(CapybaraContext historyDto, Message message) {
        return null;
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
