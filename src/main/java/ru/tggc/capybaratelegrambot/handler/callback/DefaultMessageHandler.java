package ru.tggc.capybaratelegrambot.handler.callback;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendDice;
import ru.tggc.capybaratelegrambot.aop.MessageHandleRegistry;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.DefaultMessageHandle;
import ru.tggc.capybaratelegrambot.aop.annotation.params.MessageParam;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.handler.text.TextHandler;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.sender.Sender;
import ru.tggc.capybaratelegrambot.service.impl.CapybaraService;
import ru.tggc.capybaratelegrambot.service.impl.CasinoService;
import ru.tggc.capybaratelegrambot.service.impl.HistoryService;
import ru.tggc.capybaratelegrambot.utils.HistoryType;

@BotHandler
public class DefaultMessageHandler extends TextHandler {
    private final HistoryService historyService;
    private final CasinoService casinoService;
    private final InlineKeyboardCreator inlineCreator;
    private final CapybaraService capybaraService;

    protected DefaultMessageHandler(Sender sender,
                                    MessageHandleRegistry messageHandleRegistry,
                                    HistoryService historyService,
                                    CasinoService casinoService,
                                    InlineKeyboardCreator inlineCreator,
                                    CapybaraService capybaraService) {
        super(sender, messageHandleRegistry);
        this.historyService = historyService;
        this.casinoService = casinoService;
        this.inlineCreator = inlineCreator;
        this.capybaraService = capybaraService;
    }

    @DefaultMessageHandle
    public void handleDefaultMessages(@MessageParam Message message) {
        String chatId = message.chat().id().toString();
        String userId = message.from().id().toString();
        String text = message.text();
        CapybaraContext historyDto = new CapybaraContext(chatId, userId);
        HistoryType historyType = historyService.getFromHistory(historyDto);

        switch (historyType) {
            case CASINO_SET_BET -> casinoSetBet(historyDto, text);
            case CHANGE_NAME -> changeName(historyDto, text);
            case CHANGE_PHOTO -> changePhoto(historyDto, message);
            case SLOTS_SET_BET -> slots(historyDto);
        }

        historyService.removeFromHistory(historyDto);
    }

    private void slots(CapybaraContext historyDto) {
        SendDice sendDice = new SendDice(historyDto.chatId());
        Message response = create(sendDice.slotMachine()).send().message();

        casinoService.slots(historyDto, response);
    }

    private void changeName(CapybaraContext historyDto, String text) {
//        capybaraService.changeName(historyDto, text);
    }

    private void changePhoto(CapybaraContext historyDto, Message message) {

    }

    private void casinoSetBet(CapybaraContext historyDto, String text) {
        casinoService.setBet(historyDto, text);
//        sendSimpleMessage(historyDto.chatId(), "type a target", inlineCreator.casinoTargetKeyboard());
    }
}
