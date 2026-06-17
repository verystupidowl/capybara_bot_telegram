package ru.tggc.botapp.handler.botadded;

import lombok.RequiredArgsConstructor;
import ru.tggc.botapp.keyboard.KeyboardFactory;
import ru.tggc.botapp.keyboard.KeyboardKey;
import ru.tggc.botapp.util.Text;
import ru.tggc.telegrambotframework.annotation.handle.BotAddedHandle;
import ru.tggc.telegrambotframework.annotation.handle.BotHandler;
import ru.tggc.telegrambotframework.annotation.params.Ctx;
import ru.tggc.telegrambotframework.dto.Response;
import ru.tggc.telegrambotframework.dto.UpdateContext;
import ru.tggc.telegrambotframework.handler.Handler;

@BotHandler
@RequiredArgsConstructor
public class BotAddedHandler extends Handler {
    private final KeyboardFactory keyboardFactory;

    @BotAddedHandle
    public Response botAdded(@Ctx UpdateContext ctx) {
        return sendSimpleMessage(
                ctx.chatId(),
                Text.GREETINGS,
                keyboardFactory.getKeyboardInline(KeyboardKey.TAKE_CAPYBARA)
        );
    }
}
