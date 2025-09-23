package ru.tggc.capybaratelegrambot.handler.text;

import ru.tggc.capybaratelegrambot.aop.MessageHandleRegistry;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.MessageHandle;
import ru.tggc.capybaratelegrambot.aop.annotation.params.ChatId;
import ru.tggc.capybaratelegrambot.aop.annotation.params.Ctx;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.dto.MyCapybaraDto;
import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.domain.dto.TopCapybaraDto;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.mapper.MyCapybaraMapper;
import ru.tggc.capybaratelegrambot.sender.Sender;
import ru.tggc.capybaratelegrambot.service.impl.CapybaraService;
import ru.tggc.capybaratelegrambot.utils.Text;

import java.util.List;

@BotHandler
public class CommandHandler extends TextHandler {
    private final CapybaraService capybaraService;
    private final MyCapybaraMapper myCapybaraMapper;
    private final InlineKeyboardCreator inlineCreator;

    protected CommandHandler(Sender sender,
                             MessageHandleRegistry messageHandleRegistry,
                             CapybaraService capybaraService,
                             MyCapybaraMapper myCapybaraMapper,
                             InlineKeyboardCreator inlineCreator) {
        super(sender, messageHandleRegistry);
        this.capybaraService = capybaraService;
        this.myCapybaraMapper = myCapybaraMapper;
        this.inlineCreator = inlineCreator;
    }

    @MessageHandle("/command_list")
    public void sendCommandList(@ChatId String chatId) {
        sendSimpleMessage(chatId, Text.LIST_OF_COMMANDS, null);
    }

    @MessageHandle("/my_capybara")
    public void myCapybara(@Ctx CapybaraContext ctx) {
        Capybara capybara = capybaraService.getCapybaraByContext(ctx);
        MyCapybaraDto dto = myCapybaraMapper.toDto(capybara);
        sendSimpleMessage(ctx.chatId(), Text.getMyCapybara(dto), inlineCreator.myCapybaraKeyboard(myCapybaraMapper));
    }

    @MessageHandle("/top_capybar")
    public void top(@ChatId String chatId) {
        List<TopCapybaraDto> topCapybaras = capybaraService.getTopCapybaras();
        PhotoDto photo = topCapybaras.getFirst().photoDto();
        String caption = topCapybaras.stream()
                .map(TopCapybaraDto::name)
                .reduce("", (c1, c2) -> c1 + c2);
        sendSimplePhoto(new PhotoDto(photo.getUrl(), caption, chatId, null));
    }

    @MessageHandle("/take_capybara")
    public void takeCapybara(@Ctx CapybaraContext ctx) {
        PhotoDto photoDto = capybaraService.saveCapybara(ctx);
        sendSimplePhoto(photoDto);
    }
}
