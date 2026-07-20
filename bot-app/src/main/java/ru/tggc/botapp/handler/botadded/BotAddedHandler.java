package ru.tggc.botapp.handler.botadded;

import ru.tggc.botapp.formatter.msgkey.CommonMsgKey;
import ru.tggc.botapp.keyboard.KeyboardFactory;
import ru.tggc.botapp.keyboard.KeyboardKey;
import ru.tggc.telegrambotcore.annotation.handle.BotAddedHandle;
import ru.tggc.telegrambotcore.annotation.handle.BotHandler;
import ru.tggc.telegrambotcore.annotation.params.Ctx;
import ru.tggc.telegrambotcore.dto.Response;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.formatter.FormatService;

@BotHandler
public record BotAddedHandler(KeyboardFactory keyboardFactory, FormatService formatService) {
    @BotAddedHandle
    public Response botAdded(@Ctx UpdateContext ctx) {
        return ctx.send(
                formatService.get(CommonMsgKey.GREETINGS),
                keyboardFactory.getKeyboardInline(KeyboardKey.TAKE_CAPYBARA)
        );
    }
}
