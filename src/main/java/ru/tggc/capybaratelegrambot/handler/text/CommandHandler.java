package ru.tggc.capybaratelegrambot.handler.text;

import lombok.RequiredArgsConstructor;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.BotHandler;
import ru.tggc.capybaratelegrambot.aop.annotation.handle.MessageHandle;
import ru.tggc.capybaratelegrambot.aop.annotation.params.ChatId;
import ru.tggc.capybaratelegrambot.aop.annotation.params.Ctx;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.dto.MyCapybaraDto;
import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.domain.dto.TopCapybaraDto;
import ru.tggc.capybaratelegrambot.domain.dto.response.Response;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.service.CapybaraService;
import ru.tggc.capybaratelegrambot.utils.Text;

import java.util.List;

@BotHandler
@RequiredArgsConstructor
public class CommandHandler extends TextHandler {
    private final CapybaraService capybaraService;
    private final InlineKeyboardCreator inlineCreator;

    @MessageHandle("/command_list@capybara_pet_bot")
    public Response sendCommandList(@ChatId long chatId) {
        return sendSimpleMessage(chatId, Text.LIST_OF_COMMANDS);
    }

    @MessageHandle("/my_capybara@capybara_pet_bot")
    public Response myCapybara(@Ctx CapybaraContext ctx) {
        MyCapybaraDto dto = capybaraService.getMyCapybara(ctx);
        PhotoDto photoDto = PhotoDto.builder()
                .url(dto.photo())
                .caption(Text.getMyCapybara(dto))
                .markup(inlineCreator.myCapybaraKeyboard(dto))
                .chatId(ctx.chatId())
                .build();
        return sendSimplePhoto(photoDto);
    }

    @MessageHandle("/top_capybar@capybara_pet_bot")
    public Response top(@ChatId long chatId) {
        List<TopCapybaraDto> topCapybaras = capybaraService.getTopCapybaras();
        PhotoDto photo = topCapybaras.getFirst().photoDto();
        String caption = topCapybaras.stream()
                .map(TopCapybaraDto::name)
                .reduce("", (c1, c2) -> c1 + c2);
        return sendSimplePhoto(new PhotoDto(photo.getUrl(), caption, chatId, null));
    }

    @MessageHandle("/take_capybara@capybara_pet_bot")
    public Response takeCapybara(@Ctx CapybaraContext ctx) {
        PhotoDto photoDto = capybaraService.saveCapybara(ctx);
        return sendSimplePhoto(photoDto);
    }
}
