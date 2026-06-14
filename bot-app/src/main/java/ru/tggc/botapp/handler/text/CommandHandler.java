package ru.tggc.botapp.handler.text;

import lombok.RequiredArgsConstructor;
import ru.tggc.botapp.domain.dto.MyCapybaraDto;
import ru.tggc.botapp.domain.dto.TopCapybaraDto;
import ru.tggc.botapp.keyboard.KeyboardFactory;
import ru.tggc.botapp.keyboard.KeyboardKey;
import ru.tggc.botapp.service.CapybaraService;
import ru.tggc.botapp.util.Text;
import ru.tggc.botapp.util.TextBuilder;
import ru.tggc.telegrambotframework.annotation.handle.BotHandler;
import ru.tggc.telegrambotframework.annotation.handle.CommandHandle;
import ru.tggc.telegrambotframework.annotation.params.ChatId;
import ru.tggc.telegrambotframework.annotation.params.Ctx;
import ru.tggc.telegrambotframework.dto.PhotoDto;
import ru.tggc.telegrambotframework.dto.Response;
import ru.tggc.telegrambotframework.dto.UpdateContext;

import java.util.List;
import java.util.stream.Collectors;

@BotHandler
@RequiredArgsConstructor
public class CommandHandler extends TextHandler {
    private final CapybaraService capybaraService;
    private final KeyboardFactory keyboardFactory;

    @CommandHandle(value = "start", canPrivate = true, canPublic = false)
    public Response start(@ChatId long chatId) {
        PhotoDto photoDto = PhotoDto.builder()
                .chatId(chatId)
                .caption(Text.START)
                .url("https://hi-news.ru/wp-content/uploads/2025/07/spokoimaya-kapibara-1-e1752078102391-750x523.jpg")
                .build();
        return sendSimplePhoto(photoDto);
    }

    @CommandHandle(value = "command_list", canPrivate = true)
    public Response sendCommandList(@ChatId long chatId) {
        return sendSimpleMessage(chatId, Text.LIST_OF_COMMANDS);
    }

    @CommandHandle("/my_capybara")
    public Response myCapybara(@Ctx UpdateContext ctx) {
        MyCapybaraDto dto = capybaraService.getMyCapybara(ctx);
        PhotoDto photoDto = PhotoDto.builder()
                .url(dto.photo())
                .caption(TextBuilder.getMyCapybara(dto))
                .markup(keyboardFactory.getKeyboardInline(KeyboardKey.MY_CAPYBARA, dto))
                .chatId(ctx.chatId())
                .build();
        return sendSimplePhoto(photoDto);
    }

    @CommandHandle(value = "top_capybar", canPrivate = true)
    public Response top(@ChatId long chatId) {
        List<TopCapybaraDto> topCapybaras = capybaraService.getTopCapybaras();
        PhotoDto photo = topCapybaras.getFirst().photoDto();
        String caption = topCapybaras.stream()
                .map(c -> c.name() + " - " + c.level())
                .collect(Collectors.joining("\n"));
        return sendSimplePhoto(new PhotoDto(photo.getUrl(), caption, chatId, null));
    }

    @CommandHandle("take_capybara")
    public Response takeCapybara(@Ctx UpdateContext ctx) {
        PhotoDto photoDto = capybaraService.saveCapybara(ctx);
        return sendSimplePhoto(photoDto);
    }
}
