package ru.tggc.capybaratelegrambot.handler.text;

import com.pengrad.telegrambot.model.Message;
import lombok.RequiredArgsConstructor;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.DefaultMessageHandle;
import ru.tggc.capybaratelegrambot.aop.annotation.params.MessageParam;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
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

        Response response = switch (historyType) {
            case CASINO_SET_BET -> casinoSetBet(historyDto, text);
            case CHANGE_NAME -> changeName(historyDto, text);
            case CHANGE_PHOTO -> changePhoto(historyDto, message);
            case SLOTS_SET_BET -> slots(historyDto);
            default-> throw new UnsupportedOperationException();
        };

        historyService.removeFromHistory(historyDto);
        return response;
    }

    private Response slots(CapybaraContext historyDto) {
//        SendDice sendDice = new SendDice(historyDto.chatId());
//        Message response = se(sendDice.slotMachine()).send().message();
//
//        casinoService.slots(historyDto, response);
        return null;
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
        return sendSimpleMessage(historyDto.chatId(), "type a target", inlineCreator.casinoTargetKeyboard());
    }
}
