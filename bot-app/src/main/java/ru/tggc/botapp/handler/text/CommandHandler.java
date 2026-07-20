package ru.tggc.botapp.handler.text;

import ru.tggc.botapp.domain.dto.MyCapybaraDto;
import ru.tggc.botapp.domain.dto.TopCapybaraDto;
import ru.tggc.botapp.formatter.common.CapybaraFormatter;
import ru.tggc.botapp.formatter.msgkey.CommonMsgKey;
import ru.tggc.botapp.keyboard.KeyboardFactory;
import ru.tggc.botapp.keyboard.KeyboardKey;
import ru.tggc.botapp.service.CapybaraService;
import ru.tggc.botapp.service.CommonService;
import ru.tggc.telegrambotcore.annotation.handle.BotHandler;
import ru.tggc.telegrambotcore.annotation.handle.CommandHandle;
import ru.tggc.telegrambotcore.annotation.params.Ctx;
import ru.tggc.telegrambotcore.dto.PhotoDto;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.formatter.FormatService;

import java.util.List;
import java.util.stream.Collectors;

@BotHandler
public record CommandHandler(CapybaraService capybaraService,
                             KeyboardFactory keyboardFactory,
                             CommonService commonService,
                             CapybaraFormatter capybaraFormatter,
                             FormatService formatService) {
    @CommandHandle(value = "start", canPrivate = true, canPublic = false)
    public Response start(@Ctx UpdateContext ctx) {
        return ctx.send(commonService.start(ctx.chatId()));
    }

    @CommandHandle(value = "command_list", canPrivate = true)
    public Response sendCommandList(@Ctx UpdateContext ctx) {
        return ctx.send(formatService.get(CommonMsgKey.LIST_OF_COMMANDS));
    }

    @CommandHandle("my_capybara")
    public Response myCapybara(@Ctx UpdateContext ctx) {
        MyCapybaraDto dto = capybaraService.getMyCapybara(ctx);
        PhotoDto photoDto = new PhotoDto(
                dto.photo(),
                capybaraFormatter.getMyCapybara(dto),
                ctx.chatId(),
                keyboardFactory.getKeyboardInline(KeyboardKey.MY_CAPYBARA, dto)
        );
        return ctx.send(photoDto);
    }

    @CommandHandle(value = "top_capybar", canPrivate = true)
    public Response top(@Ctx UpdateContext ctx) {
        List<TopCapybaraDto> topCapybaras = capybaraService.getTopCapybaras();
        PhotoDto photo = topCapybaras.getFirst().photoDto();
        String caption = topCapybaras.stream()
                .map(c -> c.name() + " - " + c.level())
                .collect(Collectors.joining("\n"));
        return ctx.send(new PhotoDto(photo.url(), caption));
    }

    @CommandHandle("take_capybara")
    public Response takeCapybara(@Ctx UpdateContext ctx) {
        return ctx.send(capybaraService.saveCapybara(ctx));
    }
}
